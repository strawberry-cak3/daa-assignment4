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
    static Map<String,Object> merge(Map<String,Object> input) { throw new UnsupportedOperationException("TODO merge"); }
    /** Choose a maximum-value set when neighbouring vertices are incompatible and conflicts form a tree. Implement tree according to docs/guide/CONTRACTS.md. */
    static Map<String,Object> tree(Map<String,Object> input) { throw new UnsupportedOperationException("TODO tree"); }
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
