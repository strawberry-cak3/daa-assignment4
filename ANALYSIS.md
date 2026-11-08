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

After constructing the suffix array, the inverse suffix array is built. Kasai's algorithm then calculates the LCP array, where `lcp[i]` is the longest common prefix of suffixes `sa[i-1]` and `sa[i]`. The LCP array stores the common-prefix lengths of adjacent suffixes in lexicographical order.

The algorithm then finds the maximum value in the LCP array. The corresponding pair of adjacent suffixes shares the longest repeated substring in the original text. If the maximum LCP value is zero, there is no non-empty repeated substring.

**Correctness:**

The prefix-doubling algorithm sorts suffixes by progressively longer prefixes. At each iteration, the pair of ranks represents the order of prefixes twice as long as in the previous iteration. Once all suffixes have distinct ranks, their relative order is fully determined. Therefore, the resulting suffix array is in lexicographical order.

Kasai's algorithm calculates the LCP value for every pair of adjacent suffixes in this order. The longest common prefix of any two suffixes cannot be longer than the LCP of some adjacent suffixes between them in lexicographical order. Consequently, the maximum value in the LCP array gives the length of the longest repeated substring. Selecting the corresponding suffix prefix reconstructs a valid longest repeated substring.

**Complexity:**

- Suffix array construction: `O(n * log^2 n)` using prefix doubling and comparison-based sorting.
- LCP array construction: `O(n)` using Kasai's algorithm.
- Finding the longest repeated substring: `O(n)`.
- Total time: `O(n * log^2 n)`.
- Auxiliary space: `O(n)`.

Here, `n` is the length of the input string. Kasai's algorithm is linear because it reuses the previously computed common-prefix length and decreases it by at most one when moving to the next suffix.

### 2.8. Aho–Corasick Multiple-Pattern Matching

**Approach:** Trie, failure links, BFS-based automaton construction, and visit-count propagation.

The algorithm builds one trie containing all dictionary patterns. Each trie node represents a prefix of one or more patterns.

Each node stores:
- `next`: an array of 95 transitions for printable ASCII characters (codes 32–126).
- `fail`: a failure link pointing to the longest proper suffix of the current trie prefix that is also a trie prefix.

The root is node `0`. Missing transitions are initially represented by `-1`.

#### Automaton construction

First, every pattern is inserted into the trie. The terminal node of each pattern is stored in the `terminal` array. If multiple patterns are identical, they share the same terminal node, but each retains its original dictionary position.

Next, failure links and complete transitions are constructed using BFS.

All missing root transitions are set to the root. The failure link of every root child is also set to the root.

For each subsequent node `v` and character `c`:

- If a child `u` exists, its failure link is set to the transition from `fail[v]` using the same character:

  `fail[u] = next[fail[v]][c]`

- If no child exists, the missing transition is filled using the corresponding transition of the failure state:

  `next[v][c] = next[fail[v]][c]`

Because BFS processes shallower nodes before deeper nodes, the required transitions of each failure state have already been constructed.

After this process, every state has a defined transition for every printable ASCII character.

#### Searching and counting occurrences

Each input text is processed independently.

For every text, the algorithm creates a new `visits` array initialized to zero and resets the current automaton state to the root.

For each character in the text:

1. Convert the character to its alphabet index by subtracting 32.
2. Follow the corresponding automaton transition.
3. Increment the visit count of the resulting state.

After scanning the entire text, visit counts are propagated through failure links in reverse BFS order:

`visits[fail[v]] += visits[v]`

This ensures that every state receives the visits of all states for which it is a failure ancestor.

Finally, the algorithm constructs one output row in the original dictionary order. For each pattern `i`, its occurrence count is:

`visits[terminal[i]]`

The result is a two-dimensional `counts` array. Rows correspond to input texts, and columns correspond to the original pattern positions.

#### Correctness

The trie represents every prefix of every dictionary pattern. A failure link identifies the longest proper suffix of the current prefix that is also represented in the trie.

During text scanning, the current automaton state represents the longest suffix of the processed text prefix that is a trie prefix. Consequently, when a dictionary pattern ends at the current text position, its terminal state is either the current state or a state reachable through the failure-link chain.

The algorithm increments the visit count of the current state for every text position. Processing states in reverse BFS order propagates each visit to all relevant failure ancestors. Therefore, the terminal node of a pattern receives exactly the number of text positions at which that pattern ends.

This approach counts overlapping occurrences and suffix-pattern matches without explicitly enumerating every match.

Duplicate patterns share a terminal node, so they receive the same occurrence count while remaining separate columns in the output. Each text gets a fresh visit array and starts from the root, preventing counts from one text from affecting another.

For an empty text, all pattern counts are zero. For an empty dictionary, the algorithm returns one empty row for each text. If the texts array is empty, the result is an empty array.

Therefore, the algorithm correctly counts all occurrences of every dictionary pattern in every independent text.

#### Complexity

Let:

- `L` be the total length of all patterns, including duplicates.
- `p` be the number of dictionary patterns.
- `S` be the number of trie states, including the root.
- `q` be the number of input texts.
- `|text|` be the length of an individual text.

**Automaton construction:**

- Trie insertion: `O(L)`.
- BFS failure-link construction: `O(95 * S)`, because every state processes all 95 alphabet transitions.
- Terminal-node storage: `O(p)`.

Total construction time: `O(L + 95 * S + p)`.

**Processing one text:**

- Text scanning: `O(|text|)`.
- Visit-count propagation: `O(S)`.
- Producing the count row: `O(p)`.

Total time per text: `O(|text| + S + p)`.

**Overall time:**

`O(L + 95 * S + p + sum(|text| + S + p))`

where the sum is over all input texts.

**Auxiliary space:** `O(95 * S + p)` for the trie transitions, failure links, BFS queue, terminal-node mapping, and visit counters.

**Output space:** `O(q * (p + 1) + 1)` for the count matrix.

The algorithm reuses one constructed automaton for all texts and does not enumerate individual pattern occurrences. This makes it suitable for dense matching cases within the published constraints.

## Testing

All implemented algorithms were tested using custom student test cases.

Test groups:
- LCS
- Merge (Interval DP)
- Tree DP
- Naive String Matching
- Knuth-Morris-Pratt (KMP)
- Rabin-Karp
- Suffix Array and Kasai LCP
- Aho-Corasick

### Edge Cases
The tests cover:
- Empty inputs
- Single-element inputs
- Repeated characters
- Multiple occurrences of patterns
- Patterns not found in text
- Overlapping matches
- Large integer values (64-bit safety)
- Multiple requests in a single execution

### Reproduction commands

The following commands were executed from the repository root:

```bash
python tools/run.py --setup
python tools/run.py --check
python tools/check_student.py
### Results
Language: Java
Checking student tests...

PASS aho
PASS kmp
PASS lcs
PASS merge
PASS naive
PASS rk
PASS suffix
PASS tree

------------------------------
ALL STUDENT TESTS PASSED (8 files)
```

The benchmark was compiled and executed separately:
```bash
javac --release 17 -encoding UTF-8 -d Java/build Java/source/Algorithms.java Java/source/Benchmark.java
java -cp Java/build Benchmark
```
### PUBLIC TESTING RESULTS

Note: Recompile with -Xlint:unchecked for details.
Language: Java
- PASS public 1: example-ping
- PASS public 2: example-lcs
- PASS public 3: example-merge
- PASS public 4: example-tree
- PASS public 5: example-naive
- PASS public 6: example-kmp
- PASS public 7: example-rk
- PASS public 8: example-suffix
- PASS public 9: example-aho
- PASS public 10: lcs-empty
- PASS public 11: lcs-tie
- PASS public 12: lcs-not-substring
- PASS public 13: merge-empty
- PASS public 14: merge-one
- PASS public 15: merge-zero
- PASS public 16: merge-64bit
- PASS public 17: tree-empty
- PASS public 18: tree-negative
- PASS public 19: tree-tie
- PASS public 20: tree-zero
- PASS public 21: naive-empty-pattern
- PASS public 22: naive-both-empty
- PASS public 23: naive-long-pattern
- PASS public 24: naive-overlap
- PASS public 25: naive-case-spaces
- PASS public 26: kmp-empty-pattern
- PASS public 27: kmp-both-empty
- PASS public 28: kmp-long-pattern
- PASS public 29: kmp-overlap
- PASS public 30: kmp-case-spaces
- PASS public 31: rk-empty-pattern
- PASS public 32: rk-both-empty
- PASS public 33: rk-long-pattern
- PASS public 34: rk-overlap
- PASS public 35: rk-case-spaces
- PASS public 36: rk-collision
- PASS public 37: suffix-empty
- PASS public 38: suffix-one
- PASS public 39: suffix-overlap
- PASS public 40: suffix-order
- PASS public 41: aho-empty-dictionary
- PASS public 42: aho-no-texts
- PASS public 43: aho-overlap-duplicate
- PASS public 44: aho-suffix
- PASS state reset: two complete batches in one process

### Benchmark experiment: Naive, KMP and Rabin–Karp

#### Environment and methodology

The benchmark was executed using Java 17.0.12 on Windows 11, with an AMD64 architecture and 16 available processors.

The experiment compares three string-matching algorithms: Naive, Knuth–Morris–Pratt (KMP), and Rabin–Karp (RK).

Two types of input data were tested:

- **Random:** text and pattern were independently generated using printable ASCII characters. A fixed random seed (`42`) was used to make the experiment reproducible.
- **Repetitive:** the text consisted entirely of the character `a`. The pattern consisted of `m - 1` occurrences of `a`, followed by `b`. This makes the Naive algorithm perform many character comparisons before detecting a mismatch.

Three input sizes were tested:

| Text length (n) | Pattern length (m) |
|---:|---:|
| 500 | 50 |
| 1500 | 100 |
| 3000 | 200 |

All input sizes satisfy the assignment constraints of `n, m <= 3000`.

Each algorithm was warmed up with 30 calls before measurement. Five measured batches were performed, with 100 calls per batch. The median time per operation was calculated from the five measurements.

Compilation was performed separately and excluded from the measured execution time. The benchmark measured algorithm method calls, including result construction.

Before timing, the results produced by all three algorithms were compared for each input case. All six correctness checks passed.

#### Results

All values are median execution times in milliseconds per operation.

| Input type | n | m | Naive (ms) | KMP (ms) | RK (ms) |
|---|---:|---:|---:|---:|---:|
| Random | 500 | 50 | 0.011627 | 0.011532 | 0.014733 |
| Repetitive | 500 | 50 | 0.096846 | 0.034708 | 0.016146 |
| Random | 1500 | 100 | 0.003227 | 0.009886 | 0.056983 |
| Repetitive | 1500 | 100 | 0.332728 | 0.009224 | 0.010253 |
| Random | 3000 | 200 | 0.011443 | 0.010523 | 0.022275 |
| Repetitive | 3000 | 200 | 1.160421 | 0.021746 | 0.021594 |

#### Discussion

The results demonstrate that practical execution time depends on the input characteristics.

On random inputs, the Naive algorithm can be competitive with KMP because mismatches are often detected after examining only a small number of characters. Therefore, the algorithm does not necessarily perform the maximum possible number of comparisons.

On repetitive inputs, the Naive algorithm becomes substantially slower as the input size increases. The pattern shares a long prefix with the text, so many alignments require almost `m` character comparisons before a mismatch is found. This illustrates its worst-case time complexity of `O(n * m)`.

KMP performs more consistently on repetitive inputs. Its prefix-function-based preprocessing allows it to avoid repeating comparisons, resulting in linear time complexity `O(n + m)`.

Rabin–Karp showed competitive results on some repetitive cases, but was slower than KMP on the tested random cases. Its execution includes rolling-hash calculations and verification when hash values match. Hash collisions can require additional character comparisons.

The measurements support the theoretical distinction between the algorithms: Naive can perform well on favorable inputs but has a quadratic worst-case bound, while KMP provides a linear worst-case bound. Rabin–Karp performance depends on hashing and verification behavior.

The experiment does not prove asymptotic complexity by itself. It provides empirical observations for the tested inputs and execution environment.

#### Limitations

The results are specific to the tested Java version, machine, JVM execution conditions, and input data. JIT compilation, garbage collection, and other runtime activity can influence individual measurements.

The benchmark uses a limited set of input sizes and patterns. Therefore, the results should be interpreted as an empirical comparison rather than a formal proof of algorithmic complexity.

The Rabin–Karp implementation uses the assignment's modulus of 101. This relatively small modulus can cause more hash collisions than a larger production hash space, potentially affecting performance.

## Sources and AI Assistance

The assignment specification, published contracts, practical rubric, and course lecture materials were used as the primary references for this work.

ChatGPT (OpenAI) was used as an educational and coding assistant to explain algorithm concepts, suggest and help develop parts of the Java implementations, debug implementation issues, review test cases, and improve the English algorithm analysis and benchmark documentation.

I reviewed and integrated the suggestions, executed the public and custom test suites, and checked the implementation against the published assignment contracts. The final implementation, testing, and submission were reviewed by me.</WritingBlock>
