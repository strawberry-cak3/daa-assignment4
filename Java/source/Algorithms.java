import java.util.*;
/** Implement these algorithms. docs/guide/CONTRACTS.md defines each field and limit. */
public final class Algorithms {
    /** Compare two versions of a sequence while preserving the order of shared characters. Implement lcs according to docs/guide/CONTRACTS.md. */
    static Map<String, Object> lcs(Map<String, Object> input) {
        String S1 = (String) input.get("a");
        String S2 = (String) input.get("b");

        int L1 = S1.length(); // m
        int L2 = S2.length(); // n

        int[][] table = new int[L1 + 1][L2 + 1];

        for (int i = 0; i <= L1; i++) {
            for (int j = 0; j <= L2; j++) {
                if (i == 0 || j == 0) {
                    table[i][j] = 0;
                } else if (S1.charAt(i - 1) == S2.charAt(j - 1)) {
                    table[i][j] = table[i - 1][j - 1] + 1;
                } else {
                    table[i][j] = Math.max(table[i - 1][j], table[i][j - 1]);
                }
            }
        }

        // Length of LCS
        int index = table[L1][L2];
        int length = index;

        // Array to recreate sequence
        char[] lcs = new char[index];

        int i = L1, j = L2;
        while (i > 0 && j > 0) {
            if (S1.charAt(i - 1) == S2.charAt(j - 1)) {
                lcs[index - 1] = S1.charAt(i - 1);

                i--;
                j--;
                index--;
            } else if (table[i - 1][j] > table[i][j - 1]) {
                i--;
            } else {
                j--;
            }
        }
        Map<String, Object> result = new HashMap<>();

        result.put("length", length);
        result.put("sequence", new String(lcs));

        return result;
    }

    /** Plan the cheapest sequence of adjacent block merges when the original order must be preserved. Implement merge according to docs/guide/CONTRACTS.md. */
    private static long[] readSizes(Object rawSizes) {
        List<?> list = (List<?>) rawSizes;

        long[] sizes = new long[list.size()];

        for (int i = 0; i < list.size(); i++) {
            sizes[i] = ((Number) list.get(i)).longValue();
        }

        return sizes;
    }
    private static void buildPlan(
            int l,
            int r,
            int[][] split,
            List<List<Integer>> plan) {

        if (l >= r) {
            return;
        }

        int k = split[l][r];

        buildPlan(l, k, split, plan);

        buildPlan(k + 1, r, split, plan);

        List<Integer> merge = new ArrayList<>();

        merge.add(l);
        merge.add(k);
        merge.add(r);

        plan.add(merge);
    }

    static Map<String, Object> merge(Map<String, Object> input) {
        long[] sizes = readSizes(input.get("sizes"));
        int n = sizes.length;

        Map<String, Object> result = new HashMap<>();

        if (n <= 1) {
            result.put("cost", 0L);
            result.put("plan", new ArrayList<List<Integer>>());
            return result;
        }

        long[][] dp = new long[n][n];
        int[][] split = new int[n][n];

        long[] prefixSum = new long[n + 1];

        for (int i = 0; i < n; i++) {
            prefixSum[i + 1] = prefixSum[i] + sizes[i];
        }

        for (int len = 2; len <= n; len++) {

            for (int l = 0; l <= n - len; l++) {

                int r = l + len - 1;

                long rangeSum = prefixSum[r + 1] - prefixSum[l];

                dp[l][r] = Long.MAX_VALUE;

                for (int k = l; k < r; k++) {

                    long cost = dp[l][k]
                            + dp[k + 1][r]
                            + rangeSum;

                    if (cost < dp[l][r]) {
                        dp[l][r] = cost;
                        split[l][r] = k;
                    }
                }
            }
        }

        List<List<Integer>> plan = new ArrayList<>();

        buildPlan(0, n - 1, split, plan);

        result.put("cost", dp[0][n - 1]);
        result.put("plan", plan);

        return result;
    }

    static Map<String, Object> tree(Map<String, Object> input) {

        // 1. amount
        int n = ((Number) input.get("n")).intValue();

        Map<String, Object> result = new HashMap<>();

        // 2. empty tree
        if (n == 0) {
            result.put("weight", 0L);
            result.put("selected", new ArrayList<Integer>());
            return result;
        }

        // 3. getting weight
        List<?> rawWeights = (List<?>) input.get("weights");

        long[] weights = new long[n];

        for (int i = 0; i < n; i++) {
            weights[i] = ((Number) rawWeights.get(i)).longValue();
        }

        // 4. listing
        List<List<Integer>> graph = new ArrayList<>();

        for (int i = 0; i < n; i++) {
            graph.add(new ArrayList<>());
        }

        List<?> edges = (List<?>) input.get("edges");

        for (Object edgeObject : edges) {
            List<?> edge = (List<?>) edgeObject;

            int u = ((Number) edge.get(0)).intValue();
            int v = ((Number) edge.get(1)).intValue();

            graph.get(u).add(v);
            graph.get(v).add(u);
        }

        // 5. getting ready
        int[] parent = new int[n];
        int[] order = new int[n];
        int[] stack = new int[n];

        Arrays.fill(parent, -2);

        // root - 0
        parent[0] = -1;

        int top = 0;
        int count = 0;

        stack[top++] = 0;

        // 6. Iterative DFS
        while (top > 0) {

            int v = stack[--top];

            order[count++] = v;

            for (int u : graph.get(v)) {

                // Do not get back to parent
                if (parent[u] != -2) {
                    continue;
                }

                parent[u] = v;
                stack[top++] = u;
            }
        }

        // 7. DP arrays
        long[] take = new long[n];
        long[] skip = new long[n];

        // 8. DP from bottom to top
        for (int i = count - 1; i >= 0; i--) {

            int v = order[i];

            // if v
            take[v] = weights[v];

            // if !v
            skip[v] = 0;

            for (int u : graph.get(v)) {

                // only children of v
                if (parent[u] != v) {
                    continue;
                }

                take[v] += skip[u];

                skip[v] += Math.max(take[u], skip[u]);
            }
        }

        // 9. recreate
        List<Integer> selected = new ArrayList<>();

        boolean[] isSelected = new boolean[n];

        for (int i = 0; i < count; i++) {

            int v = order[i];

            if (parent[v] == -1) {

                // for root best option
                isSelected[v] = take[v] > skip[v];

            } else if (!isSelected[parent[v]]) {

                // if parent isn't taken
                // then take this
                isSelected[v] = take[v] > skip[v];
            }

            if (isSelected[v]) {
                selected.add(v);
            }
        }

        // 10. result
        long answer = Math.max(take[0], skip[0]);

        result.put("weight", answer);
        result.put("selected", selected);

        return result;
    }
    /** Establish a simple exact-search baseline against which the other two searches can be checked. Implement naive according to docs/guide/CONTRACTS.md. */
    static Map<String,Object> naive(Map<String,Object> input) { throw new UnsupportedOperationException("TODO naive"); }
    /** Reuse information about matched prefixes to avoid restarting the search after every mismatch. Implement kmp according to docs/guide/CONTRACTS.md. */
    static Map<String,Object> kmp(Map<String,Object> input) { throw new UnsupportedOperationException("TODO kmp"); }
    /** Reject windows with unequal fingerprints and verify every equal-hash candidate. Implement rk according to docs/guide/CONTRACTS.md. */
    static Map<String,Object> rk(Map<String,Object> input) { throw new UnsupportedOperationException("TODO rk"); }
    /** Order the text suffixes and use adjacent common prefixes to find repeated substrings. Implement suffix according to docs/guide/CONTRACTS.md. */
    static Map<String,Object> suffix(Map<String,Object> input) { throw new UnsupportedOperationException("TODO suffix"); }
    /** Build one dictionary automaton and count its patterns in several independent texts. Implement aho according to docs/guide/CONTRACTS.md. */
    static Map<String,Object> aho(Map<String,Object> input) { throw new UnsupportedOperationException("TODO aho"); }
}
