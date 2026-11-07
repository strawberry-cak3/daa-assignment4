# Student Test Suite

## 1. Overview

This directory contains custom test cases designed to verify the correctness and robustness of the implemented algorithms.

Each test file contains a collection of JSON test cases. Every case includes:

- **Input** — the request sent to the program.
- **Expected output** — the result required by the algorithm's contract.
- **Name** — a short description of the scenario being tested.

The test cases focus on normal operation, boundary conditions, edge cases, and situations that may expose implementation errors.

## 2. How to Run

From the project root directory, run:

```bash
python tools/check_student.py
```

The script builds the selected implementation, executes the student test files, and compares actual results with expected results.

A test group passes when all its test cases satisfy the published contract.

## 3. Test Groups

### 3.1 Longest Common Subsequence (LCS)

**Test file:** `lcs.json`

**Purpose:** Verify that the implementation correctly calculates the length of the Longest Common Subsequence and reconstructs a valid subsequence.

| Test case | Input (`a`, `b`) | Expected output | Actual result |
|---|---|---|---|
| Both empty | `""`, `""` | Length: 0, sequence: `""` | PASS |
| First empty | `""`, `"abc"` | Length: 0, sequence: `""` | PASS |
| Second empty | `"abc"`, `""` | Length: 0, sequence: `""` | PASS |
| No common characters | `"ABC"`, `"DEF"` | Length: 0, sequence: `""` | PASS |
| Simple subsequence | `"ABCDEF"`, `"ACE"` | Length: 3, sequence: `"ACE"` | PASS |
| Subsequence, not substring | `"ABCDE"`, `"AECBD"` | Length: 3, sequence: `"ABD"` | PASS |
| Repeated characters | `"AAAA"`, `"AA"` | Length: 2, sequence: `"AA"` | PASS |
| Classic LCS example | `"ABCBDAB"`, `"BDCABA"` | Length: 4, sequence: `"BCBA"` | PASS |
| Common suffix | `"HELLO"`, `"YELLOW"` | Length: 4, sequence: `"ELLO"` | PASS |
| Multiple optimal answers | `"ABAB"`, `"BABA"` | Length: 3, sequence: `"ABA"` | PASS |

**Result:** All 10 LCS test cases passed.

The expected sequence is one valid optimal answer. When multiple longest common subsequences exist, other valid sequences of the same maximum length are also acceptable under the assignment contract.
