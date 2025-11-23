#include <stdio.h>
#include <string.h>
#include "asm.h"

int main() {

    int med;
    int res;

    // ---------------------- Test_NullVector ----------------------
    {
        int vec[] = {0};
        int expected = 0;   // invalid length, median ignored

        res = median(vec, 0, &med);

        printf("\nTEST NullVector\n");
        printf("Result: %d | Expected: %d\n", res, expected);

        if (res == expected)
            printf("SUCCESS\n\n");
        else
            printf("FAIL\n\n");
    }

    // ---------------------- Test_Zero ----------------------
    {
        int vec[] = {10, 0, 1};
        int expected = 1;

        res = median(vec, 3, &med);

        printf("TEST Zero\n");
        printf("Median: %d | Expected: %d | ", med, expected);

        if (med == expected)
            printf("SUCCESS\n\n");
        else
            printf("FAIL\n\n");
    }

    // ---------------------- Test_One ----------------------
    {
        int vec[] = {1000};
        int expected = 1000;

        res = median(vec, 1, &med);

        printf("TEST One\n");
        printf("Median: %d | Expected: %d | ", med, expected);

        if (med == expected)
            printf("SUCCESS\n\n");
        else
            printf("FAIL\n\n");
    }


    // ---------------------- Test_Two ----------------------
    {
        int vec[] = {-1, -3, -2};
        int expected = -2;

        res = median(vec, 3, &med);

        printf("TEST Two\n");
        printf("Median: %d | Expected: %d | ", med, expected);

        if (med == expected)
            printf("SUCCESS\n\n");
        else
            printf("FAIL\n\n");
    }

    // ---------------------- Test_Three ----------------------
    {
        int vec[] = {-1, -3, -4, -2};
        int expected = -3;

        res = median(vec, 4, &med);

        printf("TEST Three\n");
        printf("Median: %d | Expected: %d | ", med, expected);

        if (med == expected)
            printf("SUCCESS\n\n");
        else
            printf("FAIL\n\n");
    }

    // ---------------------- Test_Four ----------------------
    {
        int vec[] = {1, 1, 1, 1, 2};
        int expected = 1;

        res = median(vec, 5, &med);

        printf("TEST Four\n");
        printf("Median: %d | Expected: %d | ", med, expected);

        if (med == expected)
            printf("SUCCESS\n\n");
        else
            printf("FAIL\n\n");
    }

    // ---------------------- Test_Five ----------------------
    {
        int vec[] = {0, 3000, 10, 20, 0, 300};
        int expected = 15;

        res = median(vec, 6, &med);

        printf("TEST Five\n");
        printf("Median: %d | Expected: %d | ", med, expected);

        if (med == expected)
            printf("SUCCESS\n\n");
        else
            printf("FAIL\n\n");
    }

    return 0;
}
