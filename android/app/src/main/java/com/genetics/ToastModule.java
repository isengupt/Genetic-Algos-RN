package com.genetics;

import android.widget.Toast;

import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.WritableNativeMap;
import com.facebook.react.bridge.WritableNativeArray;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Arguments;

import java.util.Arrays;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;

public class ToastModule extends ReactContextBaseJavaModule {
    private static ReactApplicationContext reactContext;

    private static final String DURATION_SHORT_KEY = "SHORT";
    private static final String DURATION_LONG_KEY = "LONG";

    ToastModule(ReactApplicationContext context) {
        super(context);
        reactContext = context;
    }
    @Override
    public String getName() {
        return "ToastExample";
    }

    @Override
    public Map<String, Object> getConstants() {
        final Map<String, Object> constants = new HashMap<>();
        constants.put(DURATION_SHORT_KEY, Toast.LENGTH_SHORT);
        constants.put(DURATION_LONG_KEY, Toast.LENGTH_LONG);
        return constants;
    }

    @ReactMethod
    public void show(String message, Callback errorCallback,Callback successCallback ) {
successCallback.invoke("hello from native android");
        //Toast.makeText(getReactApplicationContext(), message, duration).show();
    }



    @ReactMethod
    public void deBruijin(String sequence, String [] patterns, Callback errorCallback,Callback successCallback ) {

        //successCallback.invoke(index);

        //Toast.makeText(getReactApplicationContext(), message, duration).show();
    }

    @ReactMethod
    public void naiveSearch(String sequence, String pattern, Callback errorCallback,Callback successCallback ) {
        int index = -1;
        int M = pattern.length();
        int N = sequence.length();

        for (int i = 0; i <= N - M; i++) {
            int j;
         for (j = 0; j < M; j++) {
             if (pattern.charAt(j) != sequence.charAt(i + j)) {
                 break;
             }

            }
            if (j == pattern.length()) {
                index = i;
            }

        }
        successCallback.invoke(index);

        //Toast.makeText(getReactApplicationContext(), message, duration).show();
    }

    public static void getZArray(String str, int[] Z) {
        int n = str.length();

        //construct window for prefix matching
        int L =0, R =0;

        for (int i = 1; i < n; ++i) {
            //use naive approach if i > R

            if (i > R) {
                L = R = i;

                while (R < n && str.charAt(R- L) == str.charAt(R)) {
                    R++;
                }
                Z[i] = R - L;
                R--;

            } else {
                int k = i - L;

                if (Z[k] < R - i +1) {
                    Z[i] = Z[k];
                }
                else {
                    L = i;
                    while (R < n && str.charAt(R- L) == str.charAt(R)) {
                        R++;
                    }
                    Z[i] = R - L;
                    R--;
                }
            }
        }

    }

    @ReactMethod
    public void zAlgorithm(String sequence, String pattern, Callback errorCallback,Callback successCallback) {
        String concat = pattern + "$" + sequence;
        int match = -1;
        List<Integer> matchArr = new ArrayList<Integer>();
        WritableArray array = new WritableNativeArray();
        int l = concat.length();

        int Z[] = new int[l];

        getZArray(concat, Z);

        for (int i =0; i< l; ++i) {
            if(Z[i] == pattern.length()){

                match = (i - pattern.length() - 1);
                matchArr.add(i - pattern.length() - 1);
                array.pushInt(i - pattern.length() - 1);
            }
        }
        //matchArr.add(4);
        array.pushInt(2);
        //Integer[] returnArray = new Integer[matchArr.size()];
        //returnArray = matchArr.toArray(returnArray);
        successCallback.invoke(array);



    }

    public static void computeLPSArray(String pattern, int M, int lps[]) {
        int len = 0;
        int i = 1;
        lps[0] = 0;

        while (i < M) {
            if (pattern.charAt(i) == pattern.charAt(len)) {
                len++;
                lps[i] = len;
                i++;
            }
            else {
                if (len != 0) {
                    len = lps[len -1];

                } else {
                    lps[i] = len;
                    i++;
                }
            }
        }
    }

    @ReactMethod
    public void KMPAlgorithm(String sequence, String pattern, Callback errorCallback,Callback successCallback) {
        int match = -1;

        int M = pattern.length();
        int N = sequence.length();

        int lps[] = new int[M];
        int j = 0;

        computeLPSArray(pattern, M, lps);

        int i =0;
        while (i < N)
        {
            if (pattern.charAt(j) == sequence.charAt(i)) {
                j++;
                i++;
            }
            if(j == M) {
                String diff = Integer.toString(i - j);
                match = i - j;

                j = lps[j-1];
            }

            else if (i < N && pattern.charAt(j) != sequence.charAt(i)) {
                if (j != 0) {
                    j = lps[j-1];
                } else {
                    i = i + 1;
                }
            }
        }
        successCallback.invoke(match);

    }

    public static int getNextState(char[] pat, int M,
                            int state, int x)
    {

        // If the character c is same as next
        // character in pattern,then simply
        // increment state
        if(state < M && x == pat[state])
            return state + 1;

        // ns stores the result which is next state
        int ns, i;

        // ns finally contains the longest prefix
        // which is also suffix in "pat[0..state-1]c"

        // Start from the largest possible value
        // and stop when you find a prefix which
        // is also suffix
        for (ns = state; ns > 0; ns--)
        {
            if (pat[ns-1] == x)
            {
                for (i = 0; i < ns-1; i++)
                    if (pat[i] != pat[state-ns+1+i])
                        break;
                if (i == ns-1)
                    return ns;
            }
        }

        return 0;
    }
    public static int NO_OF_CHARS = 256;

    public static void computeTF(char[] pat, int M, int TF[][])
    {
        int state, x;
        for (state = 0; state <= M; ++state)
            for (x = 0; x < NO_OF_CHARS; ++x)
                TF[state][x] = getNextState(pat, M, state, x);
    }

    public static int[][] getTF(char[] pat, int M, int TF[][])
    {
        int state, x;
        for (state = 0; state <= M; ++state)
            for (x = 0; x < NO_OF_CHARS; ++x)
                TF[state][x] = getNextState(pat, M, state, x);
        return TF;
    }



    public static WritableMap search(char[] pat, char[] txt)
    {

        List<Integer> searchMatches = new ArrayList<>();
        WritableMap map = new WritableNativeMap();
        WritableArray array = new WritableNativeArray();
        int M = pat.length;
        int N = txt.length;

        int[][] TF = new int[M+1][NO_OF_CHARS];

        computeTF(pat, M, TF);

        int[][] testTF = new int[M+1][NO_OF_CHARS];

        int[][] arrTF = getTF(pat, M, testTF);
        WritableArray tfArr = new WritableNativeArray();
        for (int row = 0; row < arrTF.length; row++) {
            WritableArray tfRow = new WritableNativeArray();

            for (int col = 0; col < arrTF[row].length; col++) {
                    tfRow.pushInt(arrTF[row][col]);

            }
            tfArr.pushArray(tfRow);
        }

        map.putArray("Test", tfArr);
        //map.putString("Automata", Arrays.deepToString(arrTF));

        // Process txt over FA.
        int i, state = 0;
        for (i = 0; i < N; i++)
        {
            state = TF[state][txt[i]];
            if (state == M)
               // System.out.println("Pattern found "
                    //    + "at index " + (i-M+1));
                array.pushInt(i-M+1);
        }
        //Integer[] searchArr = new Integer[searchMatches.size()];
        //searchArr = searchMatches.toArray(searchArr);
        map.putArray("Indexes", array);
        return map;
    }

    @ReactMethod
    public void Automata(String text, String pattern, Callback errorCallback,Callback successCallback) {
        //int match = -1;

        char [] pat = pattern.toCharArray();
        char [] txt = text.toCharArray();

       WritableMap result = search(txt, pat);


        successCallback.invoke(result);

    }

    public final static int d = 256;

    public static WritableMap RabinKarpSearch(String pat, String txt, int q)
    {
        int M = pat.length();
        int N = txt.length();
        int i, j;
        int p = 0; // hash value for pattern
        int t = 0; // hash value for txt
        int h = 1;

        WritableMap map = new WritableNativeMap();
        WritableArray array = new WritableNativeArray();

        // The value of h would be "pow(d, M-1)%q"
        for (i = 0; i < M-1; i++)
            h = (h*d)%q;

        // Calculate the hash value of pattern and first
        // window of text
        for (i = 0; i < M; i++)
        {
            p = (d*p + pat.charAt(i))%q;
            t = (d*t + txt.charAt(i))%q;
        }

        // Slide the pattern over text one by one
        for (i = 0; i <= N - M; i++)
        {

            // Check the hash values of current window of text
            // and pattern. If the hash values match then only
            // check for characters on by one
            if ( p == t )
            {
                /* Check for characters one by one */
                for (j = 0; j < M; j++)
                {
                    if (txt.charAt(i+j) != pat.charAt(j))
                        break;
                }

                // if p == t and pat[0...M-1] = txt[i, i+1, ...i+M-1]
                if (j == M)
                    System.out.println("Pattern found at index " + i);
                    array.pushInt(i);
            }

            // Calculate hash value for next window of text: Remove
            // leading digit, add trailing digit
            if ( i < N-M )
            {
                t = (d*(t - txt.charAt(i)*h) + txt.charAt(i+M))%q;

                // We might get negative value of t, converting it
                // to positive
                if (t < 0)
                    t = (t + q);
            }
        }
        map.putArray("Indexes", array);
        return map;
    }

    @ReactMethod
    public void RabinKarpAlgorithm(String text, String pattern, Callback errorCallback,Callback successCallback) {
        //int match = -1;

        int q = 101;

        WritableMap result = RabinKarpSearch(pattern, text, q);


        successCallback.invoke(result);

    }
    @ReactMethod
    public void findBitapPattern(String t, String p, Callback errorCallback,Callback successCallback )
    {
        char[] text = t.toCharArray();
        char[] pattern = p.toCharArray();
        int pos = bitap_search(text, pattern);
        if (pos == -1)
          successCallback.invoke(pos);
        else
           successCallback.invoke(pos);
    }

    public int bitap_search(char[] text, char[] pattern)
    {
        int m = pattern.length;
        long pattern_mask[] = new long[Character.MAX_VALUE + 1];
        /** Initialize the bit array R **/
        long R = ~1;
        if (m == 0)
            return -1;
        if (m > 63)
        {
            System.out.println("Pattern is too long!");
            return -1;
        }
        /** Initialize the pattern bitmasks **/
        for (int i = 0; i <= Character.MAX_VALUE; ++i)
            pattern_mask[i] = ~0;
        for (int i = 0; i < m; ++i)
            pattern_mask[pattern[i]] &= ~(1L << i);
        for (int i = 0; i < text.length; ++i)
        {
            /** Update the bit array **/
            R |= pattern_mask[text[i]];
            R <<= 1;
            if ((R & (1L << m)) == 0)
                return i - m + 1;
        }
        return -1;
    }

    public class SuffixArray {
        private String[] text;
        private int length;
        public int[] index;
        public String[] suffix;


        public SuffixArray(String text) {
            this.text = new String[text.length()];

            for (int i = 0; i < text.length(); i++) {
                this.text[i] = text.substring(i, i + 1);
            }

            this.length = text.length();
            this.index = new int[length];
            for (int i = 0; i < length; i++) {
                index[i] = i;
            }

            suffix = new String[length];
        }

        public void createSuffixArray() {
            for (int index = 0; index < length; index++) {
                String text = "";
                for (int text_index = index; text_index < length; text_index++) {
                    text += this.text[text_index];
                }
                suffix[index] = text;
            }

            int back;
            for (int iteration = 1; iteration < length; iteration++) {
                String key = suffix[iteration];
                int keyindex = index[iteration];

                for (back = iteration - 1; back >= 0; back--) {
                    if (suffix[back].compareTo(key) > 0) {
                        suffix[back + 1] = suffix[back];
                        index[back + 1] = index[back];
                    } else {
                        break;
                    }
                }
                suffix[back + 1] = key;
                index[back + 1] = keyindex;
            }

            System.out.println("SUFFIX \t INDEX");
            for (int iterate = 0; iterate < length; iterate++) {
                System.out.println(suffix[iterate] + "\t" + index[iterate]);

            }
        }
    }



    @ReactMethod
    public void getSuffixArray(String text, Callback errorCallback,Callback successCallback) {


        SuffixArray suffixarray = new SuffixArray(text);
        suffixarray.createSuffixArray();
        String[] sufStrings = suffixarray.suffix;
        int[] sufInts = suffixarray.index;
        WritableMap map = new WritableNativeMap();
        for (int iterate = 0; iterate < sufStrings.length; iterate++) {
            //System.out.println(suffix[iterate] + "\t" + index[iterate]);
            map.putInt(sufStrings[iterate], sufInts[iterate]);
        }




        successCallback.invoke(map);

    }

    static boolean WildcardMatch(String str, String pattern,
                            int n, int m)
    {
        // empty pattern can only match with
        // empty string
        if (m == 0)
            return (n == 0);

        // lookup table for storing results of
        // subproblems
        boolean[][] lookup = new boolean[n + 1][m + 1];

        // initailze lookup table to false
        for(int i = 0; i < n + 1; i++)
            Arrays.fill(lookup[i], false);


        // empty pattern can match with empty string
        lookup[0][0] = true;

        // Only '*' can match with empty string
        for (int j = 1; j <= m; j++)
            if (pattern.charAt(j - 1) == '*')
                lookup[0][j] = lookup[0][j - 1];

        // fill the table in bottom-up fashion
        for (int i = 1; i <= n; i++)
        {
            for (int j = 1; j <= m; j++)
            {
                // Two cases if we see a '*'
                // a) We ignore '*'' character and move
                //    to next  character in the pattern,
                //     i.e., '*' indicates an empty sequence.
                // b) '*' character matches with ith
                //     character in input
                if (pattern.charAt(j - 1) == '*')
                    lookup[i][j] = lookup[i][j - 1] ||
                            lookup[i - 1][j];

                    // Current characters are considered as
                    // matching in two cases
                    // (a) current character of pattern is '?'
                    // (b) characters actually match
                else if (pattern.charAt(j - 1) == '?' ||
                        str.charAt(i - 1) == pattern.charAt(j - 1))
                    lookup[i][j] = lookup[i - 1][j - 1];

                    // If characters don't match
                else lookup[i][j] = false;
            }
        }

        return lookup[n][m];
    }


    @ReactMethod
    public void getWildCardMatch(String text, String pattern, Callback errorCallback,Callback successCallback) {


        Boolean matched = WildcardMatch(text, pattern, text.length(), pattern.length());



        successCallback.invoke(matched);

    }













}