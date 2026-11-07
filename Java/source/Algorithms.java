import java.util.*;

public final class Algorithms {
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

        // Один элемент — сливать нечего
        if (l >= r) {
            return;
        }

        // Узнаём, где оптимально разделить интервал
        int k = split[l][r];

        // Сначала выполняем слияния левой половины
        buildPlan(l, k, split, plan);

        // Затем выполняем слияния правой половины
        buildPlan(k + 1, r, split, plan);

        // Теперь можно объединить обе половины
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

    static Map<String, Object> naive(Map<String, Object> input) {

        // 1. Получаем текст и образец
        String text = (String) input.get("text");
        String pattern = (String) input.get("pattern");

        int n = text.length();
        int m = pattern.length();

        // 2. list of found
        List<Integer> positions = new ArrayList<>();

        // 3. empty
        if (m == 0) {

            for (int i = 0; i <= n; i++) {
                positions.add(i);
            }

        } else if (m <= n) {

            // 4. every possible coincidence
            for (int i = 0; i <= n - m; i++) {

                boolean match = true;

                // 5. compare
                for (int j = 0; j < m; j++) {

                    if (text.charAt(i + j) != pattern.charAt(j)) {
                        match = false;
                        break;
                    }
                }

                // 6. if match - hold
                if (match) {
                    positions.add(i);
                }
            }
        }

        // 7. result
        Map<String, Object> result = new HashMap<>();

        result.put("positions", positions);

        return result;
    }

    private static int[] buildPrefixFunction(String pattern) {

        int m = pattern.length();

        int[] pi = new int[m];

        int j = 0;

        for (int i = 1; i < m; i++) {

            // if !match
            // try shorter
            while (j > 0 && pattern.charAt(i) != pattern.charAt(j)) {
                j = pi[j - 1];
            }

            // match -> make longer
            if (pattern.charAt(i) == pattern.charAt(j)) {
                j++;
            }

            pi[i] = j;
        }

        return pi;
    }
    static Map<String, Object> kmp(Map<String, Object> input) {

        // 1. text & sample
        String text = (String) input.get("text");
        String pattern = (String) input.get("pattern");

        int n = text.length();
        int m = pattern.length();

        List<Integer> positions = new ArrayList<>();

        // 2. empty
        if (m == 0) {

            for (int i = 0; i <= n; i++) {
                positions.add(i);
            }

        } else {

            // 3. prefix func
            int[] pi = buildPrefixFunction(pattern);

            // j — how many match
            int j = 0;

            // 4. through text
            for (int i = 0; i < n; i++) {

                // if !match,
                // use prefix
                while (j > 0 && text.charAt(i) != pattern.charAt(j)) {
                    j = pi[j - 1];
                }

                // if match
                if (text.charAt(i) == pattern.charAt(j)) {
                    j++;
                }

                // if match all sample
                if (j == m) {

                    positions.add(i - m + 1);

                    // ready for search
                    // next
                    j = pi[j - 1];
                }
            }
        }

        // 5. result
        Map<String, Object> result = new HashMap<>();

        result.put("positions", positions);

        return result;
    }


    static Map<String, Object> rk(Map<String, Object> input) {

        String text = (String) input.get("text");
        String pattern = (String) input.get("pattern");

        int n = text.length();
        int m = pattern.length();

        List<Integer> positions = new ArrayList<>();

        // empty pattern
        if (m == 0) {
            for (int i = 0; i <= n; i++) {
                positions.add(i);
            }

            Map<String, Object> result = new HashMap<>();
            result.put("positions", positions);
            return result;
        }

        // pattern longer than text
        if (m > n) {
            Map<String, Object> result = new HashMap<>();
            result.put("positions", positions);
            return result;
        }

        final int BASE = 31;
        final int MOD = 101;

        // BASE^(m-1) % MOD
        int highestPower = 1;

        for (int i = 0; i < m - 1; i++) {
            highestPower = (highestPower * BASE) % MOD;
        }

        int patternHash = 0;
        int windowHash = 0;

        // first hash
        for (int i = 0; i < m; i++) {

            patternHash =
                    (patternHash * BASE + pattern.charAt(i)) % MOD;

            windowHash =
                    (windowHash * BASE + text.charAt(i)) % MOD;
        }

        // all windows of text
        for (int i = 0; i <= n - m; i++) {

            // match -> check symbols
            if (patternHash == windowHash) {

                boolean match = true;

                for (int j = 0; j < m; j++) {

                    if (text.charAt(i + j) != pattern.charAt(j)) {
                        match = false;
                        break;
                    }
                }

                if (match) {
                    positions.add(i);
                }
            }

            // update hash for new window
            if (i < n - m) {

                windowHash =
                        windowHash
                                - (text.charAt(i) * highestPower) % MOD;

                windowHash = (windowHash * BASE
                        + text.charAt(i + m)) % MOD;

                // Java can return negative mod
                if (windowHash < 0) {
                    windowHash += MOD;
                }
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("positions", positions);

        return result;
    }

    static Map<String, Object> suffix(Map<String, Object> input) {
        String text = (String) input.get("text");
        int n = text.length();

        Map<String, Object> result = new HashMap<>();

        List<Integer> saList = new ArrayList<>();
        List<Integer> lcpList = new ArrayList<>();

        // empty
        if (n == 0) {
            result.put("sa", saList);
            result.put("lcp", lcpList);
            result.put("repeated", "");
            return result;
        }

        // 1. Initialize suffix array and ranks
        int[] sa = new int[n];
        int[] rank = new int[n];

        for (int i = 0; i < n; i++) {
            sa[i] = i;
            rank[i] = text.charAt(i);
        }

        // 2. Build suffix array using prefix doubling
        for (int k = 1; k < n; k *= 2) {

            Integer[] order = new Integer[n];

            for (int i = 0; i < n; i++) {
                order[i] = sa[i];
            }

            final int[] currentRank = rank;

            int finalK = k;
            Arrays.sort(order, (a, b) -> {
                if (currentRank[a] != currentRank[b]) {
                    return Integer.compare(currentRank[a], currentRank[b]);
                }

                int rankA = (a + finalK < n) ? currentRank[a + finalK] : -1;
                int rankB = (b + finalK < n) ? currentRank[b + finalK] : -1;

                return Integer.compare(rankA, rankB);
            });

            int[] newRank = new int[n];
            int classes = 0;

            newRank[order[0]] = 0;

            for (int i = 1; i < n; i++) {
                int previous = order[i - 1];
                int current = order[i];

                int previousSecond =
                        (previous + k < n) ? rank[previous + k] : -1;

                int currentSecond =
                        (current + k < n) ? rank[current + k] : -1;

                if (rank[previous] != rank[current]
                        || previousSecond != currentSecond) {
                    classes++;
                }

                newRank[current] = classes;
            }

            for (int i = 0; i < n; i++) {
                sa[i] = order[i];
            }

            rank = newRank;

            if (classes == n - 1) {
                break;
            }
        }

        // 3. LCP array using Kasai algorithm
        int[] lcp = new int[n];
        int[] inverse = new int[n];

        for (int i = 0; i < n; i++) {
            inverse[sa[i]] = i;
        }

        int h = 0;

        for (int i = 0; i < n; i++) {
            int position = inverse[i];

            if (position == 0) {
                h = 0;
                lcp[position] = 0;
                continue;
            }

            int j = sa[position - 1];

            while (i + h < n
                    && j + h < n
                    && text.charAt(i + h) == text.charAt(j + h)) {
                h++;
            }

            lcp[position] = h;

            if (h > 0) {
                h--;
            }
        }

        // 4. longest repeated substring
        int maxLcp = 0;
        int repeatedStart = 0;

        for (int i = 1; i < n; i++) {
            if (lcp[i] > maxLcp) {
                maxLcp = lcp[i];
                repeatedStart = sa[i];
            }
        }

        String repeated = "";

        if (maxLcp > 0) {
            repeated = text.substring(
                    repeatedStart,
                    repeatedStart + maxLcp
            );
        }

        // 5. Convert arrays to lists
        for (int i = 0; i < n; i++) {
            saList.add(sa[i]);
            lcpList.add(lcp[i]);
        }

        result.put("sa", saList);
        result.put("lcp", lcpList);
        result.put("repeated", repeated);

        return result;
    }

    private static final int ALPHABET_SIZE = 95;
    private static class AhoNode {

        // ASCII 32..126
        int[] next = new int[ALPHABET_SIZE];

        // Failure Link
        int fail = 0;

        AhoNode() {
            Arrays.fill(next, -1);
        }
    }

    static Map<String, Object> aho(Map<String, Object> input) {

        // 1. patterns & texts
        List<?> rawPatterns = (List<?>) input.get("patterns");
        List<?> rawTexts = (List<?>) input.get("texts");

        int patternCount = rawPatterns.size();
        int textCount = rawTexts.size();

        // 2. trie
        List<AhoNode> trie = new ArrayList<>();

        // root
        trie.add(new AhoNode());

        // terminal node of pattern
        int[] terminal = new int[patternCount];

        // 3. add all patterns in Trie
        for (int i = 0; i < patternCount; i++) {

            String pattern = (String) rawPatterns.get(i);

            int v = 0;

            for (int j = 0; j < pattern.length(); j++) {

                int c = pattern.charAt(j) - 32;

                int u = trie.get(v).next[c];

                // no transition -> make node
                if (u == -1) {

                    u = trie.size();

                    trie.get(v).next[c] = u;

                    trie.add(new AhoNode());
                }

                v = u;
            }

            // end pattern
            terminal[i] = v;
        }

        // 4. BFS
        int nodeCount = trie.size();

        int[] queue = new int[nodeCount];

        int head = 0;
        int tail = 0;

        // 5. transition Root
        for (int c = 0; c < ALPHABET_SIZE; c++) {

            int u = trie.get(0).next[c];

            if (u == -1) {

                // no trans -> stay in Root
                trie.get(0).next[c] = 0;

            } else {

                trie.get(u).fail = 0;

                queue[tail++] = u;
            }
        }

        // 6. build Failure Links with BFS
        while (head < tail) {

            int v = queue[head++];

            for (int c = 0; c < ALPHABET_SIZE; c++) {

                int u = trie.get(v).next[c];

                if (u != -1) {

                    // calc Failure Link
                    int failure = trie.get(v).fail;

                    trie.get(u).fail =
                            trie.get(failure).next[c];

                    queue[tail++] = u;

                } else {

                    // take trans of Failure Link
                    int failure = trie.get(v).fail;

                    trie.get(v).next[c] =
                            trie.get(failure).next[c];
                }
            }
        }

        // 7. take each text individual
        List<List<Integer>> allCounts = new ArrayList<>();

        for (int t = 0; t < textCount; t++) {

            String text = (String) rawTexts.get(t);

            // count
            int[] visits = new int[nodeCount];

            int state = 0;

            // 8. through text
            for (int i = 0; i < text.length(); i++) {

                int c = text.charAt(i) - 32;

                // next
                state = trie.get(state).next[c];

                // count of visits
                visits[state]++;
            }

            // 9. share visits Failure Links
            // BFS guarantee from start
            // taking more deep nodes

            for (int i = tail - 1; i >= 0; i--) {

                int v = queue[i];

                int failure = trie.get(v).fail;

                visits[failure] += visits[v];
            }

            // 10. num for all patterns
            List<Integer> row = new ArrayList<>();

            for (int i = 0; i < patternCount; i++) {

                row.add(visits[terminal[i]]);
            }

            allCounts.add(row);
        }

        // 11. result
        Map<String, Object> result = new HashMap<>();

        result.put("counts", allCounts);

        return result;
    }
}
