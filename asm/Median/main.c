#include <stdio.h>

int median(int* vec, int length, int *me);

int main() {
    int vec1[] = {5, 2, 9, 1, 7};
    int vec2[] = {10, 20, 30, 40};
    int vec3[] = {3};
    int vec4[] = {};
    int med;

    int res;

    res = median(vec1, 5, &med);
    printf("Result: %d, Median: %d\n", res, med); // Expected: 1, Median: 5

    res = median(vec2, 4, &med);
    printf("Result: %d, Median: %d\n", res, med); // Expected: 1, Median: 25

    res = median(vec3, 1, &med);
    printf("Result: %d, Median: %d\n", res, med); // Expected: 1, Median: 3

    res = median(vec4, 0, &med);
    printf("Result: %d\n", res); // Expected: 0, invalid length

    return 0;
}
