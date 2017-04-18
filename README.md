# expression-parser
Parse and Evaluate Mathematical Expression JSON 

Program Should take one json file and print 3 subsequent line (as described in below o/p A, B, C)

Input : 

```
{
  "op": "equal",
  "lhs": {
    "op": "subtract",
    "lhs": {
      "op": "multiply",
      "lhs": 2,
      "rhs": "x"
    },
    "rhs": 5
  },
  "rhs": 15
}
```
OutPut : 

Output-A
```
(2 * x) - 5 = 15
```
Output-B
```
x = (15 + 5)/2
```
Output-C

```
x = 10     // take Output-B as input and eveluate the result
```

