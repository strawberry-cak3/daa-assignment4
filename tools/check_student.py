import json
import pathlib
import sys

from run import build, run_cases, select_language

P = pathlib.Path(__file__).resolve().parents[1]
TEST_DIR = P / "tests" / "student"


def main():
    try:
        language = select_language(None)
        command = build(language)

        print(f"Language: {language}")
        print("Checking student tests...\n")

        files = sorted(TEST_DIR.glob("*.jsonl"))

        if not files:
            print("No test files found in tests/student/")
            return 1

        total_failures = 0

        for file in files:
            with file.open(encoding="utf-8") as stream:
                cases = json.load(stream)

            if not isinstance(cases, list):
                print(f"FAIL {file.name}: expected a JSON array")
                total_failures += 1
                continue

            failures = run_cases(command, cases, file.stem)
            total_failures += failures

        print("\n------------------------------")

        if total_failures == 0:
            print(f"ALL STUDENT TESTS PASSED ({len(files)} files)")
            return 0

        print(f"FAILED: {total_failures} test groups")
        return 1

    except Exception as error:
        print(f"ERROR: {error}")
        return 1


if __name__ == "__main__":
    sys.exit(main())