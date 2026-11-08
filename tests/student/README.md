# Student Test Suite

## 1. Overview

This directory contains 74 custom test cases covering all eight implemented algorithms.

The purpose of these tests is to verify:

- Correctness of algorithm results.
- Correct reconstruction of witnesses and solutions.
- Handling of empty and boundary inputs.
- Correct behaviour with repeated elements and overlapping matches.
- Handling of large values and 64-bit integer arithmetic.
- Correct processing of multiple independent requests and texts.

Each test case contains an input request, an expected output, and a descriptive name. The test runner compares the program's actual output with the expected result according to the assignment's testing rules.

## 2. How to Run

Run the following command from the repository root:

```bash
python tools/check_student.py
```

The script builds the selected implementation, executes the test cases in `tests/student`, and checks the actual results against the expected outputs.

A test group passes when all its test cases satisfy the expected results and the published contract.

## 3. Test Groups

### 3.1. Longest Common Subsequence (LCS)

**Test file:** `lcs.jsonl`

**Number of test cases:** 10

**Purpose:** Verify that the implementation calculates the optimal LCS length and reconstructs a valid longest common subsequence.

| Test case | Scenario | Expected result / defect detected |
|---|---|---|
| Both empty | Both input strings are empty | Length `0`, sequence `""`. Checks the base case when both strings are empty. |
| First empty | The first string is empty | Length `0`, sequence `""`. Checks handling of an empty first input. |
| Second empty | The second string is empty | Length `0`, sequence `""`. Checks handling of an empty second input. |
| No common characters | `"ABC"` and `"DEF"` | Length `0`, sequence `""`. Detects incorrect handling when no characters match. |
| Simple subsequence | `"ABCDEF"` and `"ACE"` | Length `3`, sequence `"ACE"`. Checks basic subsequence reconstruction. |
| Subsequence not substring | `"ABCDE"` and `"AECBD"` | Length `3`, sequence `"ABD"`. Detects implementations that incorrectly require the answer to be a contiguous substring. |
| Repeated characters | `"AAAA"` and `"AA"` | Length `2`, sequence `"AA"`. Checks repeated-character handling. |
| Classic LCS | `"ABCBDAB"` and `"BDCABA"` | Length `4`, sequence `"BCBA"`. Checks a standard non-trivial DP case. |
| Common suffix | `"HELLO"` and `"YELLOW"` | Length `4`, sequence `"ELLO"`. Checks a long common suffix. |
| Multiple optimal answers | `"ABAB"` and `"BABA"` | Length `3`, sequence `"ABA"`. Checks tie handling and reconstruction when several optimal answers exist. |

**Result:** 10 test cases.

The expected sequence is one valid optimal answer. Other common subsequences of the same maximum length are acceptable under the assignment contract.

### 3.2. Optimal Merging of Blocks

**Test file:** `merge.jsonl`

**Number of test cases:** 10

**Purpose:** Verify minimum merge cost calculation and reconstruction of a valid sequence of adjacent interval merges.

| Test case | Scenario | Expected result / defect detected |
|---|---|---|
| Empty array | No blocks | Cost `0`, empty plan. Checks the empty-input base case. |
| Single block | One block of size `10` | Cost `0`, empty plan. Checks that no merge is needed for one block. |
| Two blocks | Sizes `[5, 7]` | Cost `12`, plan `[[0, 0, 1]]`. Checks the simplest merge operation. |
| Basic optimal merge | Sizes `[2, 3, 4]` | Cost `14`. Checks interval DP with three blocks. |
| Merge smaller blocks first | Sizes `[3, 1, 1]` | Cost `7`, plan `[[1, 1, 2], [0, 0, 2]]`. Checks that the optimal plan respects the adjacency requirement. |
| Four blocks | Sizes `[1, 2, 3, 4]` | Cost `19`. Checks reconstruction over a larger interval. |
| Small blocks before large block | Sizes `[1, 1, 10]` | Cost `14`. Checks whether the algorithm finds the optimal adjacent merge order. |
| Zero-sized block | Sizes `[0, 5]` | Cost `5`. Checks handling of a zero-sized block. |
| Large total cost | Sizes `[1000000000, 1000000000]` | Cost `2000000000`. Checks large merge costs. |
| 64-bit integer safety | Three blocks, each of size `1000000000` | Cost `5000000000`. Detects integer overflow when calculating total costs. |

**Result:** 10 test cases.

The merge plan is checked as part of the output. It must describe valid adjacent interval merges and achieve the optimal total cost.

### 3.3. Maximum-Weight Independent Set on a Tree

**Test file:** `tree.jsonl`

**Number of test cases:** 9

**Purpose:** Verify maximum-weight independent set calculation, including correct selection of vertices and handling of negative, zero, and large weights.

| Test case | Scenario | Expected result / defect detected |
|---|---|---|
| Empty tree | No vertices | Weight `0`, selected vertices `[]`. Checks the empty-tree base case. |
| Single positive vertex | One vertex with weight `10` | Weight `10`, selected `[0]`. Checks the simplest positive solution. |
| Single negative vertex | One vertex with weight `-5` | Weight `0`, selected `[]`. Checks that a negative vertex is not selected unnecessarily. |
| Basic chain | Three vertices with weights `[4, 9, 6]` | Weight `10`, selected `[0, 2]`. Checks the independent-set constraint on adjacent vertices. |
| Star tree | Center weight `10`, leaf weights `4, 5, 6` | Weight `15`, selected `[1, 2, 3]`. Checks a tree with several children and the choice between a parent and its children. |
| All negative weights | Three negative-weight vertices | Weight `0`, selected `[]`. Checks that the empty independent set is preferred. |
| All zero weights | Three zero-weight vertices | Weight `0`, selected `[]`. Checks tie handling when selecting vertices does not improve the objective value. |
| Longer chain | Six vertices with weights `[1, 2, 3, 4, 5, 6]` | Weight `12`, selected `[1, 3, 5]`. Checks DP transitions and reconstruction over a longer path. |
| 64-bit total weight | Five vertices in a chain, each with weight `1000000000` | Weight `3000000000`, selected `[0, 2, 4]`. Checks that individual weights remain within the contract while the optimal sum requires 64-bit arithmetic. |

**Result:** 9 test cases.

The selected vertices must form a valid independent set: no two selected vertices may be connected by an edge. The selected set must also achieve the maximum possible total weight.

### 3.4. Naive String Matching

**Test file:** `naive.jsonl`

**Number of test cases:** 10

**Purpose:** Verify that the brute-force algorithm finds every occurrence of a pattern, including overlapping occurrences, and handles boundary cases correctly.

| Test case | Scenario | Expected positions / defect detected |
|---|---|---|
| Empty text and pattern | Both strings are empty | `[0]`. Checks the empty-pattern convention. |
| Empty pattern | Text `"abc"`, empty pattern | `[0, 1, 2, 3]`. Checks that an empty pattern matches every text boundary. |
| Empty text | Empty text, pattern `"a"` | `[]`. Checks that a non-empty pattern cannot match an empty text. |
| Pattern longer than text | `"abc"` and `"abcd"` | `[]`. Checks invalid starting-position handling. |
| Overlapping matches | `"aaaaa"` and `"aaa"` | `[0, 1, 2]`. Detects skipped overlapping occurrences. |
| Multiple occurrences | `"hello hello"` and `"hello"` | `[0, 6]`. Checks finding more than one occurrence. |
| Case sensitivity | `"Hello hello HELLO"` and `"hello"` | `[6]`. Detects incorrect case-insensitive matching. |
| Repeated pattern | `"abcabcabc"` and `"bc"` | `[1, 4, 7]`. Checks repeated matches at different positions. |
| Spaces in text | `"a b a b"` and `"a b"` | `[0, 4]`. Checks that spaces are treated as ordinary characters. |
| No matches | `"abcdefgh"` and `"xyz"` | `[]`. Checks unsuccessful searches. |

**Result:** 10 test cases.

### 3.5. Knuth–Morris–Pratt (KMP)

**Test file:** `kmp.jsonl`

**Number of test cases:** 10

**Purpose:** Verify prefix-function-based matching, correct fallback after mismatches, and preservation of overlapping matches.

| Test case | Scenario | Expected positions / defect detected |
|---|---|---|
| Empty text and pattern | Both strings are empty | `[0]`. Checks the empty-pattern convention. |
| Empty pattern | Text `"abc"`, empty pattern | `[0, 1, 2, 3]`. Checks all valid empty-pattern positions. |
| Empty text | Empty text, pattern `"a"` | `[]`. Checks empty-text handling. |
| Pattern longer than text | `"abc"` and `"abcd"` | `[]`. Checks boundary handling. |
| Overlapping matches | `"aaaaa"` and `"aaa"` | `[0, 1, 2]`. Detects incorrect resetting after a complete match. |
| Multiple occurrences | `"hello hello"` and `"hello"` | `[0, 6]`. Checks multiple matches. |
| Case sensitivity | `"Hello hello HELLO"` and `"hello"` | `[6]`. Checks case-sensitive matching. |
| Repeated pattern | `"abcabcabc"` and `"bc"` | `[1, 4, 7]`. Checks repeated occurrences. |
| Spaces in text | `"a b a b"` and `"a b"` | `[0, 4]`. Checks matching strings containing spaces. |
| No matches | `"abcdefgh"` and `"xyz"` | `[]`. Checks unsuccessful searches. |

**Result:** 10 test cases.

These cases are also useful for detecting errors in the prefix-function fallback logic, especially when the pattern contains repeated prefixes.

### 3.6. Rabin–Karp (RK)

**Test file:** `rk.jsonl`

**Number of test cases:** 10

**Purpose:** Verify rolling-hash matching, correct handling of overlapping occurrences, and direct character verification after a hash match.

| Test case | Scenario | Expected positions / defect detected |
|---|---|---|
| Empty text and pattern | Both strings are empty | `[0]`. Checks the empty-pattern convention. |
| Empty pattern | Text `"abc"`, empty pattern | `[0, 1, 2, 3]`. Checks all empty-pattern positions. |
| Empty text | Empty text, pattern `"a"` | `[]`. Checks empty-text handling. |
| Pattern longer than text | `"abc"` and `"abcd"` | `[]`. Checks boundary handling. |
| Overlapping matches | `"aaaaa"` and `"aaa"` | `[0, 1, 2]`. Detects skipped overlapping matches. |
| Multiple occurrences | `"hello hello"` and `"hello"` | `[0, 6]`. Checks multiple matches. |
| Case sensitivity | `"Hello hello HELLO"` and `"hello"` | `[6]`. Checks case-sensitive matching. |
| Repeated pattern | `"abcabcabc"` and `"bc"` | `[1, 4, 7]`. Checks repeated occurrences. |
| Hash collision verification | Text `"abdj"`, pattern `"ab"` | `[0]`. Checks that a hash match is verified against the actual characters. |
| Second collision window | Text `"abdj"`, pattern `"dj"` | `[2]`. Checks correct verification of another window with the same hash value. |

**Result:** 10 test cases.

The collision tests are particularly important because the implementation uses base `31` and modulus `101`. The strings `"ab"` and `"dj"` have the same polynomial hash with these parameters, even though they are different strings.

The algorithm must therefore compare the actual characters whenever the hashes match. Without this verification, it could report false matches.

### 3.7. Suffix Array and Kasai LCP

**Test file:** `suffix.jsonl`

**Number of test cases:** 7

**Purpose:** Verify lexicographical suffix ordering, LCP array construction, and reconstruction of a longest repeated substring.

| Test case | Scenario | Expected result / defect detected |
|---|---|---|
| Empty text | `""` | `sa = []`, `lcp = []`, repeated `""`. Checks the empty-input case. |
| Single character | `"a"` | `sa = [0]`, `lcp = [0]`, repeated `""`. Checks the smallest non-empty input. |
| All distinct characters | `"abc"` | `sa = [0, 1, 2]`, `lcp = [0, 0, 0]`, repeated `""`. Checks suffix ordering when there are no common prefixes. |
| All identical characters | `"aaaa"` | `sa = [3, 2, 1, 0]`, `lcp = [0, 1, 2, 3]`, repeated `"aaa"`. Checks ordering and LCP values for highly repetitive input. |
| Banana example | `"banana"` | `sa = [5, 3, 1, 0, 4, 2]`, `lcp = [0, 1, 3, 0, 0, 2]`, repeated `"ana"`. Checks a standard suffix-array example. |
| Overlapping repeated substring | `"abab"` | `sa = [2, 0, 3, 1]`, `lcp = [0, 2, 0, 1]`, repeated `"ab"`. Checks overlapping repeated substrings. |
| Complex repeated characters | `"mississippi"` | `sa = [10, 7, 4, 1, 0, 9, 8, 6, 3, 5, 2]`, `lcp = [0, 1, 1, 4, 0, 0, 1, 0, 2, 1, 3]`, repeated `"issi"`. Checks more complex suffix ordering and LCP reconstruction. |

**Result:** 7 test cases.

The suffix array and LCP array are checked together with the repeated substring. The repeated substring must be a valid longest repeated substring. When multiple optimal repeated substrings exist, alternative optimal answers are acceptable under the assignment contract.

### 3.8. Aho–Corasick Multiple-Pattern Matching

**Test file:** `aho.jsonl`

**Number of test cases:** 8

**Purpose:** Verify multiple-pattern occurrence counting using a trie and failure links. The tests cover empty inputs, overlapping occurrences, suffix matches, duplicate patterns, and independent texts.

| Test case | Scenario | Expected counts / defect detected |
|---|---|---|
| Empty dictionary | No patterns, texts `"abc"` and `"def"` | `[[], []]`. Checks handling of a dictionary with no patterns. |
| No texts | Patterns `"a"` and `"b"`, empty text list | `[]`. Checks that no output rows are produced when there are no texts. |
| Empty text | Patterns `"a"`, `"ab"`, `"abc"` and empty text | `[[0, 0, 0]]`. Checks that an empty text has zero occurrences. |
| Overlapping patterns | Patterns `"a"`, `"aa"`, `"aaa"` in `"aaaa"` | `[[4, 3, 2]]`. Detects missed overlapping occurrences. |
| Suffix matches | Patterns `"he"`, `"she"`, `"his"`, `"hers"` in `"ahishers"` | `[[1, 1, 1, 1]]`. Checks failure-link transitions and matches that are suffixes of other matched patterns. |
| Duplicate patterns and multiple texts | Patterns `"he"`, `"she"`, `"he"` in two identical texts and one empty text | `[[2, 2, 2], [2, 2, 2], [0, 0, 0]]`. Detects lost duplicate patterns and incorrect sharing of counts between texts. |
| Suffix pattern relationships | Patterns `"abc"`, `"bc"`, `"c"` in texts `"abc"`, `"bc"`, `"c"` | `[[1, 1, 1], [0, 1, 1], [0, 0, 1]]`. Checks propagation through multiple levels of failure links. |
| Independent text counts | Patterns `"a"`, `"b"`, `"ab"` in `"abab"`, `"bbbb"`, and `"aaaa"` | `[[2, 2, 2], [0, 4, 0], [4, 0, 0]]`. Detects accumulated state or counters leaking between independent texts. |

**Result:** 8 test cases.

These tests are designed to verify that:

- Failure links preserve matches that are suffixes of other trie prefixes.
- Overlapping occurrences are counted correctly.
- Duplicate dictionary entries retain their original positions in the output.
- Each text is processed independently.
- Counts are returned in the original pattern order.

## 4. Test Summary

| Algorithm | Test file | Number of cases |
|---|---|---:|
| Longest Common Subsequence | `lcs.jsonl` | 10 |
|