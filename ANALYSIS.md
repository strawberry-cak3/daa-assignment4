# Assignment 4 — Algorithm Analysis

## 1. Overview

This assignment implements dynamic programming and string-processing algorithms in Java.

The solution supports the following commands:

- `lcs` — Longest Common Subsequence.
- `merge` — Optimal merging of consecutive blocks using interval dynamic programming.
- `tree` — Maximum-weight independent set on a tree.
- `naive` — Naive string matching.
- `kmp` — Knuth–Morris–Pratt string matching.
- `rk` — Rabin–Karp string matching.
- `suffix` — Suffix array construction, LCP computation, and longest repeated substring.
- `aho` — Aho–Corasick multiple-pattern matching.

The implementation follows the assignment's JSON Lines interface. Each input request is processed independently, and the program produces one JSON result per request.

All string algorithms operate on the assignment's printable ASCII alphabet. String matching is case-sensitive, and positions are zero-based.

---

## 2. Algorithm Analysis

### 2.1. Longest Common Subsequence (LCS)

**Approach:** Dynamic programming.

Let `a` and `b` be the input strings. Define `dp[i][j]` as the length of the LCS of the first `i` characters of `a` and the first `j` characters of `b`.

The recurrence is:

- `dp[i][j] = 0` if `i = 0` or `j = 0`.
- If `a[i-1] == b[j-1]`, then `dp[i][j] = dp[i-1][j-1] + 1`.
- Otherwise, `dp[i][j] = max(dp[i-1][j], dp[i][j-1])`.

After filling the table, the algorithm reconstructs an actual common subsequence by moving backwards from `dp[m][n]`.

**Correctness:** The recurrence considers the optimal solution for every pair of prefixes. If the final characters match, they can be included in an optimal common subsequence. Otherwise, at least one of the two prefixes must be shortened. Taking the maximum of these possibilities therefore gives the optimal LCS length. Backtracking follows the same recurrence and reconstructs a valid subsequence of that length.

**Complexity:**

- Time: `O(m * n)`.
- Space: `O(m * n)`.

Here, `m` and `n` are the lengths of the input strings.

### 2.2. Optimal Merging of Blocks

**Approach:** Interval dynamic programming.

The algorithm finds the minimum total cost of merging consecutive blocks. It uses prefix sums to calculate the total size of any interval in constant time.

Let `dp[l][r]` be the minimum cost of merging blocks from index `l` to index `r`.

For an interval containing at least two blocks:

`dp[l][r] = min(dp[l][k] + dp[k+1][r] + sum(l, r))`

where `l <= k < r`.

The algorithm stores the optimal split position in a separate `split` table. After computing the minimum cost, it reconstructs the merge plan using a recursive postorder traversal: first the left interval, then the right interval, and finally their merge.

**Correctness:** The final merge of any interval must divide it into two non-empty consecutive intervals. Both intervals must themselves be merged optimally; otherwise, replacing a non-optimal subplan with an optimal one would reduce the total cost. The recurrence checks every possible split and adds the cost of the final merge. Therefore, it finds the optimal cost. The stored split positions reconstruct a valid plan that achieves this cost.

**Complexity:**

- Time: `O(n^3)`.
- Space: `O(n^2)`.

There are `O(n^2)` intervals, and each interval considers up to `O(n)` split positions.

### 2.3. Maximum-Weight Independent Set on a Tree

**Approach:** Tree dynamic programming with iterative DFS.

The algorithm first builds an adjacency list and roots the tree at vertex `0`. An iterative DFS determines the parent of every vertex and records a traversal order.

For each vertex `v`, two DP states are calculated:

- `take[v]`: maximum weight of an independent set in the subtree of `v` when `v` is selected.
- `skip[v]`: maximum weight when `v` is not selected.

The transitions are:

`take[v] = weight[v] + sum(skip[child])`

`skip[v] = sum(max(take[child], skip[child]))`

The DP is evaluated in reverse DFS order so that every child's values are available before its parent's values are calculated.

The selected vertices are reconstructed in forward order. If a parent is selected, its children cannot be selected. Otherwise, a child is selected only when its `take` value is strictly greater than its `skip` value.

**Correctness:** Every independent set either includes a vertex or excludes it. If a vertex is included, none of its children can be included. If it is excluded, each child can independently use whichever of its two states gives the larger value. These cases cover all valid independent sets in the subtree. Since the tree has no edges between different child subtrees, combining their optimal values gives the optimal subtree solution. The reconstruction follows the same constraints and produces a valid independent set with the computed maximum weight.

**Complexity:**

- Time: `O(n)`.
- Space: `O(n)`.

Every vertex and edge is processed a constant number of times.

### 2.4. Naive String Matching

**Approach:** Brute-force comparison.

The algorithm checks every possible starting position of the pattern in the text. At each position, it compares the pattern characters one by one. If all characters match, the starting position is added to the result.

The algorithm does not skip positions after a match, so overlapping occurrences are included.

An empty pattern matches at all positions from `0` through `n`.

**Correctness:** Every possible valid starting position is examined. A position is reported exactly when all characters of the pattern match the corresponding text characters. Therefore, all and only valid occurrences are returned.

**Complexity:**

- Worst-case time: `O(n * m)`.
- Auxiliary space: `O(1)`, excluding the output list.

Here, `n` is the text length and `m` is the pattern length.

### 2.5. Knuth–Morris–Pratt (KMP)

**Approach:** Prefix function and efficient pattern matching.

First, the algorithm builds the prefix function for the pattern. The value `pi[i]` represents the length of the longest proper prefix of the pattern ending at position `i` that is also a suffix.

During text scanning, if a mismatch occurs, the algorithm uses the prefix function to move the pattern position to the longest possible valid border instead of restarting the comparison.

After a complete match, it records the starting position and uses the prefix function again. This allows overlapping occurrences to be found.

An empty pattern is handled separately and matches all `n + 1` positions.

**Correctness:** The prefix function stores the longest prefix that can still match after a mismatch. Therefore, moving to `pi[j-1]` does not skip any possible occurrence. Each complete match is reported, and resetting `j` to the longest proper border preserves the possibility of overlapping matches. Thus, every occurrence is found exactly once.

**Complexity:**

- Prefix function construction: `O(m)`.
- Text scanning: `O(n)`.
- Total time: `O(n + m)`.
- Auxiliary space: `O(m)`, excluding the output list.

### 2.6. Rabin–Karp (RK)

**Approach:** Rolling polynomial hash.

The implementation uses:

- Base: `31`.
- Modulus: `101`.

It first calculates the hash of the pattern and the first text window of the same length.

When moving to the next window, it removes the contribution of the outgoing character, shifts the remaining hash by multiplying by the base, and adds the incoming character.

If the window hash equals the pattern hash, the algorithm compares the actual characters. This verification is necessary because different strings can have the same hash.

An empty pattern is handled separately.

**Correctness:** Every window of the text with the same length as the pattern is examined. Equal strings must have equal polynomial hashes, so no real match is discarded. Hash equality alone is not sufficient because collisions are possible; direct character verification ensures that only actual matches are reported. Therefore, all and only valid occurrences are returned.

**Complexity:**

- Expected time: `O(n + m)` under a suitable distribution of hash collisions.
- Worst-case time: `O(n * m)` because many windows may have matching hashes and require character verification.
- Auxiliary space: `O(1)`, excluding the output list.

### 2.7. Suffix Array and Kasai LCP

**Approach:** Prefix doubling and Kasai's algorithm.

The suffix array is constructed using the prefix-doubling technique.

Initially, every suffix is ranked by its first character. At each iteration, suffixes are sorted by a pair of ranks:

1. The rank of the first `k` characters.
2. The rank of the next `k` characters.

The represented prefix length is doubled after each iteration. New ranks are assigned according to the sorted order. The algorithm stops when all suffixes have distinct ranks.

The implementation uses Java's `Arrays.sort` with a comparator at every doubling step.

After constructing the suffix array, the inverse suffix array is built. Kasai's algorithm then calculates the LCP array, where `lcp[i]` is the longest common prefix of suffixes `sa[i-1]` and `sa[i]`. The