package com.devvault.config.seed;

import java.util.ArrayList;
import java.util.List;

/**
 * Runnable code challenges (starter code and test cases) for 30 of the problems.
 * Parameter types are limited to int, long, boolean, String and arrays of those, so the same tests work in the
 * browser (JavaScript) and in the Java judge.
 */
public final class ChallengeSeedData {

    /** One challenge definition. javaParams uses Java syntax, for example "int[] nums, int target". */
    public record Def(String problemTitle, String functionName, String javaParams, String javaReturn, String testsJson) {

        public String jsStarter() {
            StringBuilder names = new StringBuilder();
            for (String p : javaParams.split(",")) {
                String[] parts = p.trim().split("\\s+");
                if (names.length() > 0) {
                    names.append(", ");
                }
                names.append(parts[parts.length - 1]);
            }
            return "function " + functionName + "(" + names + ") {\n  // write your code here\n}\n";
        }

        public String javaStarter() {
            String fallback = switch (javaReturn) {
                case "int", "long" -> "0";
                case "boolean" -> "false";
                case "String" -> "\"\"";
                default -> "null";
            };
            return "class Solution {\n    public " + javaReturn + " " + functionName + "(" + javaParams + ") {\n"
                    + "        // write your code here\n        return " + fallback + ";\n    }\n}\n";
        }
    }

    private ChallengeSeedData() {
    }

    private static void d(List<Def> out, String title, String fn, String params, String ret, String tests) {
        out.add(new Def(title, fn, params, ret, tests));
    }

    public static List<Def> all() {
        List<Def> l = new ArrayList<>();

        d(l, "Two Sum", "twoSum", "int[] nums, int target", "int[]", """
                [{"args":[[2,7,11,15],9],"expected":[0,1]},{"args":[[3,2,4],6],"expected":[1,2]},
                 {"args":[[3,3],6],"expected":[0,1]},{"args":[[1,5,9,14],23],"expected":[2,3]},
                 {"args":[[-3,4,3,90],0],"expected":[0,2]}]""");

        d(l, "Valid Parentheses", "isValid", "String s", "boolean", """
                [{"args":["()"],"expected":true},{"args":["()[]{}"],"expected":true},{"args":["(]"],"expected":false},
                 {"args":["([)]"],"expected":false},{"args":["{[]}"],"expected":true},{"args":["(("],"expected":false},
                 {"args":[""],"expected":true}]""");

        d(l, "Reverse a String", "reverseString", "String s", "String", """
                [{"args":["hello"],"expected":"olleh"},{"args":["a"],"expected":"a"},{"args":[""],"expected":""},
                 {"args":["DevVault"],"expected":"tluaVveD"},{"args":["racecar"],"expected":"racecar"}]""");

        d(l, "Palindrome Check", "isPalindrome", "String s", "boolean", """
                [{"args":["A man, a plan, a canal: Panama"],"expected":true},{"args":["race a car"],"expected":false},
                 {"args":[""],"expected":true},{"args":[" "],"expected":true},{"args":["0P"],"expected":false},
                 {"args":["Was it a car or a cat I saw?"],"expected":true}]""");

        d(l, "FizzBuzz", "fizzBuzz", "int n", "String[]", """
                [{"args":[1],"expected":["1"]},{"args":[3],"expected":["1","2","Fizz"]},
                 {"args":[5],"expected":["1","2","Fizz","4","Buzz"]},
                 {"args":[15],"expected":["1","2","Fizz","4","Buzz","Fizz","7","8","Fizz","Buzz","11","Fizz","13","14","FizzBuzz"]}]""");

        d(l, "Find Maximum in Array", "findMax", "int[] nums", "int", """
                [{"args":[[3,1,4,1,5,9,2,6]],"expected":9},{"args":[[-5,-2,-9]],"expected":-2},{"args":[[7]],"expected":7},
                 {"args":[[0,0,0]],"expected":0},{"args":[[10,-1,10]],"expected":10}]""");

        d(l, "Sum of Digits", "digitSum", "int n", "int", """
                [{"args":[4821],"expected":15},{"args":[0],"expected":0},{"args":[9],"expected":9},
                 {"args":[100],"expected":1},{"args":[99999],"expected":45}]""");

        d(l, "Factorial", "factorial", "int n", "long", """
                [{"args":[0],"expected":1},{"args":[1],"expected":1},{"args":[5],"expected":120},
                 {"args":[10],"expected":3628800},{"args":[15],"expected":1307674368000}]""");

        d(l, "Fibonacci Number", "fib", "int n", "int", """
                [{"args":[0],"expected":0},{"args":[1],"expected":1},{"args":[2],"expected":1},{"args":[10],"expected":55},
                 {"args":[20],"expected":6765},{"args":[30],"expected":832040}]""");

        d(l, "Prime Number Check", "isPrime", "int n", "boolean", """
                [{"args":[2],"expected":true},{"args":[1],"expected":false},{"args":[0],"expected":false},
                 {"args":[17],"expected":true},{"args":[18],"expected":false},{"args":[97],"expected":true},
                 {"args":[100],"expected":false},{"args":[7919],"expected":true}]""");

        d(l, "Contains Duplicate", "containsDuplicate", "int[] nums", "boolean", """
                [{"args":[[1,2,3,1]],"expected":true},{"args":[[1,2,3,4]],"expected":false},{"args":[[]],"expected":false},
                 {"args":[[5]],"expected":false},{"args":[[1,1,1,3,3,4,3,2,4,2]],"expected":true}]""");

        d(l, "Valid Anagram", "isAnagram", "String s, String t", "boolean", """
                [{"args":["anagram","nagaram"],"expected":true},{"args":["rat","car"],"expected":false},
                 {"args":["a","a"],"expected":true},{"args":["ab","a"],"expected":false},
                 {"args":["listen","silent"],"expected":true}]""");

        d(l, "Move Zeroes", "moveZeroes", "int[] nums", "int[]", """
                [{"args":[[0,1,0,3,12]],"expected":[1,3,12,0,0]},{"args":[[0]],"expected":[0]},
                 {"args":[[1,2,3]],"expected":[1,2,3]},{"args":[[0,0,1]],"expected":[1,0,0]},
                 {"args":[[4,0,5,0,0,6]],"expected":[4,5,6,0,0,0]}]""");

        d(l, "Best Time to Buy and Sell Stock", "maxProfit", "int[] prices", "int", """
                [{"args":[[7,1,5,3,6,4]],"expected":5},{"args":[[7,6,4,3,1]],"expected":0},{"args":[[1]],"expected":0},
                 {"args":[[2,4,1]],"expected":2},{"args":[[3,2,6,5,0,3]],"expected":4}]""");

        d(l, "Climbing Stairs", "climbStairs", "int n", "int", """
                [{"args":[1],"expected":1},{"args":[2],"expected":2},{"args":[3],"expected":3},{"args":[5],"expected":8},
                 {"args":[10],"expected":89},{"args":[20],"expected":10946}]""");

        d(l, "Binary Search (Find Index)", "search", "int[] nums, int target", "int", """
                [{"args":[[-1,0,3,5,9,12],9],"expected":4},{"args":[[-1,0,3,5,9,12],2],"expected":-1},
                 {"args":[[5],5],"expected":0},{"args":[[1,3],3],"expected":1},{"args":[[],1],"expected":-1}]""");

        d(l, "Power of Two", "isPowerOfTwo", "int n", "boolean", """
                [{"args":[1],"expected":true},{"args":[16],"expected":true},{"args":[3],"expected":false},
                 {"args":[0],"expected":false},{"args":[-16],"expected":false},{"args":[1024],"expected":true},
                 {"args":[218],"expected":false}]""");

        d(l, "Single Number", "singleNumber", "int[] nums", "int", """
                [{"args":[[2,2,1]],"expected":1},{"args":[[4,1,2,1,2]],"expected":4},{"args":[[1]],"expected":1},
                 {"args":[[7,3,7]],"expected":3},{"args":[[-1,-1,-2]],"expected":-2}]""");

        d(l, "Missing Number", "missingNumber", "int[] nums", "int", """
                [{"args":[[3,0,1]],"expected":2},{"args":[[0,1]],"expected":2},
                 {"args":[[9,6,4,2,3,5,7,0,1]],"expected":8},{"args":[[0]],"expected":1},{"args":[[1]],"expected":0}]""");

        d(l, "Maximum Subarray (Kadane's Algorithm)", "maxSubArray", "int[] nums", "int", """
                [{"args":[[-2,1,-3,4,-1,2,1,-5,4]],"expected":6},{"args":[[1]],"expected":1},
                 {"args":[[5,4,-1,7,8]],"expected":23},{"args":[[-3,-1,-2]],"expected":-1},{"args":[[-2,-1]],"expected":-1}]""");

        d(l, "Merge Intervals", "merge", "int[][] intervals", "int[][]", """
                [{"args":[[[1,3],[2,6],[8,10],[15,18]]],"expected":[[1,6],[8,10],[15,18]]},
                 {"args":[[[1,4],[4,5]]],"expected":[[1,5]]},{"args":[[[1,4],[0,4]]],"expected":[[0,4]]},
                 {"args":[[[1,4],[2,3]]],"expected":[[1,4]]},{"args":[[[5,6],[1,2]]],"expected":[[1,2],[5,6]]}]""");

        d(l, "Product of Array Except Self", "productExceptSelf", "int[] nums", "int[]", """
                [{"args":[[1,2,3,4]],"expected":[24,12,8,6]},{"args":[[-1,1,0,-3,3]],"expected":[0,0,9,0,0]},
                 {"args":[[2,3]],"expected":[3,2]},{"args":[[0,0]],"expected":[0,0]},{"args":[[5,1,1]],"expected":[1,5,5]}]""");

        d(l, "Longest Common Prefix", "longestCommonPrefix", "String[] strs", "String", """
                [{"args":[["flower","flow","flight"]],"expected":"fl"},{"args":[["dog","racecar","car"]],"expected":""},
                 {"args":[["a"]],"expected":"a"},{"args":[["ab","ab"]],"expected":"ab"},{"args":[["",""]],"expected":""}]""");

        d(l, "Roman to Integer", "romanToInt", "String s", "int", """
                [{"args":["III"],"expected":3},{"args":["LVIII"],"expected":58},{"args":["MCMXCIV"],"expected":1994},
                 {"args":["IV"],"expected":4},{"args":["IX"],"expected":9},{"args":["XL"],"expected":40}]""");

        d(l, "Majority Element", "majorityElement", "int[] nums", "int", """
                [{"args":[[3,2,3]],"expected":3},{"args":[[2,2,1,1,1,2,2]],"expected":2},{"args":[[1]],"expected":1},
                 {"args":[[6,6,6,7,7]],"expected":6}]""");

        d(l, "Coin Change", "coinChange", "int[] coins, int amount", "int", """
                [{"args":[[1,2,5],11],"expected":3},{"args":[[2],3],"expected":-1},{"args":[[1],0],"expected":0},
                 {"args":[[1,3,4],6],"expected":2},{"args":[[186,419,83,408],6249],"expected":20}]""");

        d(l, "House Robber", "rob", "int[] nums", "int", """
                [{"args":[[1,2,3,1]],"expected":4},{"args":[[2,7,9,3,1]],"expected":12},{"args":[[5]],"expected":5},
                 {"args":[[2,1,1,2]],"expected":4},{"args":[[]],"expected":0}]""");

        d(l, "Rotate Array", "rotate", "int[] nums, int k", "int[]", """
                [{"args":[[1,2,3,4,5,6,7],3],"expected":[5,6,7,1,2,3,4]},{"args":[[-1,-100,3,99],2],"expected":[3,99,-1,-100]},
                 {"args":[[1],5],"expected":[1]},{"args":[[1,2],3],"expected":[2,1]},{"args":[[1,2,3],0],"expected":[1,2,3]}]""");

        d(l, "Count Vowels in a Username", "countVowels", "String s", "int", """
                [{"args":["Rajesh"],"expected":2},{"args":["DEVVAULT"],"expected":3},{"args":["rhythm"],"expected":0},
                 {"args":["AEIOUaeiou"],"expected":10},{"args":[""],"expected":0}]""");

        d(l, "Remove Duplicates from Sorted Array", "removeDuplicates", "int[] nums", "int", """
                [{"args":[[1,1,2]],"expected":2},{"args":[[0,0,1,1,1,2,2,3,3,4]],"expected":5},{"args":[[1]],"expected":1},
                 {"args":[[1,2,3]],"expected":3},{"args":[[]],"expected":0}]""");

        return l;
    }
}
