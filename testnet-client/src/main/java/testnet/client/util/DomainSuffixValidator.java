package testnet.client.util;

import java.io.*;
import java.net.*;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.*;
import java.util.logging.*;
import org.xbill.DNS.*;
import org.xbill.DNS.Record;

public class DomainSuffixValidator {
    private static final Logger LOGGER = Logger.getLogger(DomainSuffixValidator.class.getName());
    private static final int NUM_THREADS = 50;  // Number of threads to use
    private static final int TIMEOUT_SECONDS = 3;  // DNS lookup timeout

    public static void main(String[] args) throws UnknownHostException, ExecutionException, InterruptedException {
        Resolver resolver = new SimpleResolver("223.5.5.5");
        Duration timeoutDuration = Duration.ofSeconds(2);
        DNSResult result = getDNSRecords("akadns.net", resolver);
        System.out.println(result.getCname() == null);
        System.out.println(result.getARecords().length == 0);
    }
//    public static void main(String[] args) {
//
//
//        String inputFile = "D:\\Code\\JeecgBoot\\jeecg-boot\\testnet-common\\src\\main\\resources\\public_suffix_list.dat";
//        String outputFile = "D:\\Code\\JeecgBoot\\jeecg-boot\\testnet-common\\src\\main\\resources\\suffix_list.dat";
//
//        List<String> suffixes = readSuffixFile(inputFile);
//        if (suffixes.isEmpty()) {
//            LOGGER.severe("No suffixes found in input file or error reading file");
//            return;
//        }
//
//        LOGGER.info("Found " + suffixes.size() + " suffixes to validate");
//
//        // Create thread pool and result collection
//        ExecutorService executor = Executors.newFixedThreadPool(NUM_THREADS);
//        List<Future<Boolean>> resultList = new ArrayList<>();
//        List<String> validSuffixes = Collections.synchronizedList(new ArrayList<>());
//
//        // Process each suffix
//        for (String suffix : suffixes) {
//            Callable<Boolean> validator = () -> validateSuffix(suffix, validSuffixes);
//            Future<Boolean> future = executor.submit(validator);
//            resultList.add(future);
//        }
//
//        // Wait for all threads to complete
//        for (Future<Boolean> future : resultList) {
//            try {
//                future.get();
//            } catch (InterruptedException | ExecutionException e) {
//                LOGGER.severe("Error waiting for thread completion: " + e.getMessage());
//            }
//        }
//
//        executor.shutdown();
//
//        LOGGER.info("Validation complete. Found " + validSuffixes.size() + " valid top-level domains");
//
//        // Write valid suffixes to output file
//        writeSuffixFile(validSuffixes, outputFile);
//    }

    private static List<String> readSuffixFile(String fileName) {
        List<String> suffixes = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // Skip empty lines and comments
                if (line.trim().isEmpty() || line.startsWith("//")) {
                    continue;
                }
                suffixes.add(line.trim());
            }
        } catch (IOException e) {
            LOGGER.severe("Error reading input file: " + e.getMessage());
        }
        return suffixes;
    }

    private static boolean validateSuffix(String suffix, List<String> validSuffixes) {
        // Skip wildcard entries
        if (suffix.startsWith("*")) {
            LOGGER.fine("Skipping wildcard entry: " + suffix);
            return false;
        }

        // Skip entries with ! prefix (exception rules in PSL)
        if (suffix.startsWith("!")) {
            LOGGER.fine("Skipping exception rule: " + suffix);
            return false;
        }

        try {

            Resolver resolver = new SimpleResolver("223.5.5.5");
            Duration timeoutDuration = Duration.ofSeconds(2);
            resolver.setTimeout(timeoutDuration);
            DNSResult result = getDNSRecords(suffix, resolver);
            // Try to resolve the suffix


            if (result.getCname() == null && result.getARecords().length == 0) {
                // No resolution means it's likely a valid TLD
                LOGGER.info("Valid TLD found: " + suffix);
                validSuffixes.add(suffix);
                return true;
            } else {
                // If it resolves, it's not a pure TLD
                LOGGER.fine("Not a TLD (resolves to IP): " + suffix);
                return false;
            }
        } catch (Exception e) {
            // Usually, UnknownHostException means it doesn't resolve
            // which is what we want for a TLD
            LOGGER.info("Valid TLD found: " + suffix);
            validSuffixes.add(suffix);
            return true;
        }
    }

    private static InetAddress[] resolveWithTimeout(String hostname, int timeoutSeconds) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<InetAddress[]> future = executor.submit(() -> {
            try {
                return InetAddress.getAllByName(hostname);
            } catch (UnknownHostException e) {
                return null;
            }
        });

        try {
            return future.get(timeoutSeconds, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            future.cancel(true);
            LOGGER.fine("DNS lookup timeout for: " + hostname);
            return null;
        } catch (Exception e) {
            future.cancel(true);
            LOGGER.fine("Error in DNS lookup for: " + hostname + " - " + e.getMessage());
            return null;
        } finally {
            executor.shutdownNow();
        }
    }

    private static void writeSuffixFile(List<String> suffixes, String fileName) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            for (String suffix : suffixes) {
                writer.write(suffix);
                writer.newLine();
            }
            LOGGER.info("Successfully wrote " + suffixes.size() + " valid TLDs to " + fileName);
        } catch (IOException e) {
            LOGGER.severe("Error writing output file: " + e.getMessage());
        }
    }

    public static DNSResult getDNSRecords(final String domain, final Resolver resolver) throws InterruptedException, ExecutionException {
        ExecutorService executor = Executors.newFixedThreadPool(2);

        Callable<String> cnameTask = new Callable<String>() {
            @Override
            public String call() throws Exception {
                return getCNAMERecord(domain, resolver);
            }
        };

        Callable<String[]> aTask = new Callable<String[]>() {
            @Override
            public String[] call() throws Exception {
                return getARecords(domain, resolver);
            }
        };

        Future<String> cnameFuture = executor.submit(cnameTask);
        Future<String[]> aFuture = executor.submit(aTask);

        String cname = (String) cnameFuture.get();
        String[] aRecords = (String[]) aFuture.get();

        executor.shutdown();

        return new DNSResult(cname, aRecords);
    }

    public static String getCNAMERecord(String domain, Resolver resolver) throws Exception {
        Lookup lookup = new Lookup(domain, Type.CNAME);
        lookup.setResolver(resolver);
        lookup.run();
        if (lookup.getResult() == Lookup.SUCCESSFUL) {
            Record[] records = lookup.getAnswers();
            return records.length > 0 ? records[0].rdataToString() : null;
        }
        return null;
    }

    public static String[] getARecords(String domain, Resolver resolver) throws Exception {
        Lookup lookup = new Lookup(domain, Type.A);
        lookup.setResolver(resolver);
        lookup.run();
        if (lookup.getResult() == Lookup.SUCCESSFUL) {
            Record[] records = lookup.getAnswers();
            String[] ips = new String[records.length];
            for (int i = 0; i < records.length; i++) {
                ips[i] = records[i].rdataToString();
            }
            return ips;
        }
        return new String[0];
    }

    private static class DNSResult {
        private final String cname;
        private final String[] aRecords;

        public DNSResult(String cname, String[] aRecords) {
            this.cname = cname;
            this.aRecords = aRecords;
        }

        public String getCname() {
            return cname;
        }

        public String[] getARecords() {
            return aRecords;
        }
    }
}