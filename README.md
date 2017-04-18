# expression-parser
Parse and Evaluate Mathematical Expression JSON 

For Example :

Input : 
<code>
{
  "op": "equal",
  "lhs": {
    "op": "subtract",
    "rhs": {
      "op": "multiply",
      "lhs": 2,
      "rhs": "x"
    },
    "lhs": 5
  },
  "rhs": 15
}
</code>
