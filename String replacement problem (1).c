#include <stdio.h>
#include <stdlib.h>


// defining macros for min: for two numbers if a>b returning b, else - returning a
#define min(a, b) ((a) > (b) ? (b) : (a))


// Here we will put our answer into text file
void writing(char *answer) {
    // Initializing output file, putting mode "writing" on it
    char *filename = "output.txt";
    FILE *openfile = fopen(filename, "w");
    fputs(answer, openfile);
    fclose(openfile);
}


// Function which checks uniqueness of each element in array
int frequency(int len, int arr[len], int member) {
    int counter = 0;
    for (int i = 0; i < len; ++i) {
        if (arr[i] == member) {
            ++counter;
        }
    }
    return counter;
    // if counter != 1, we have duplicated element -> they're not unique -> wrong input
}


int main() {
    // n - length of string, m = length of an array
    int n, m;

    // alphabet
    // " " in alph is needed because our count starts from 0, so "a" is the first elem
    char alph[27] = " abcdefghijklmnopqrstuvwxyz";

    // characters which are allowed in string
    char allowed[72] = "abcdefghijklmnopqrstuvwxyz"
                       "ABCDEFGHIJKLMNOPQRSTUVWXYZ()*!@#$%&^0123456789";

    // Initializing input file
    FILE *file = fopen("input.txt", "r");
    fscanf(file, "%d", &n);


    // Checking if N has valid bounds
    if (n < 2 || n > 50) {
        writing("Invalid inputs\n");
        return EXIT_SUCCESS;
    }

    // Declaring and scanning string
    char s[n];
    fscanf(file, "%s", s);

    // Checking if S consists of allowed symbols
    for (int i = 0; i < n; ++i) {
        int count = 0;
        for (int j = 0; j < 72; ++j) {
            if (s[i] == allowed[j]) {
                ++count;
            }
        }
        if (count == 0) {
            writing("Invalid inputs\n");
            return EXIT_SUCCESS;
        }
    }

    fscanf(file, "%d", &m);

    // Checking if M has valid bounds
    if (m < 1 || m > min(n - 1, 26)) {
        writing("Invalid inputs\n");
        return EXIT_SUCCESS;
    }


    // Declaring an array, filling it
    int a[m];
    for (int i = 0; i < m; ++i) {
        fscanf(file, "%d", &a[i]);
    }

    // looking for maximum element in the array a
    int maximum = -10000000;
    for (int i = 0; i < m; ++i) {
        if (a[i] > maximum) {
            maximum = a[i];
        }
    }
    // Checking if maximum element from the array a is less than the length of string
    if (n <= maximum) {
        writing("Invalid inputs\n");
        return EXIT_SUCCESS;
    }

    // Checking if our array consists of unique elements and if all of them are in range [1, 26]
    for (int i = 0; i < m; ++i) {
        if (frequency(m, a, a[i]) != 1) {
            writing("Invalid inputs\n");
            return EXIT_SUCCESS;
        }
        if (a[i] < 1 || a[i] > 26) {
            writing("Invalid inputs\n");
            return EXIT_SUCCESS;
        }
    }

    // Program itself: replacing i element in string s with i element from alphabet
    for (int i = 0; i < m; ++i) {
        s[a[i]] = alph[a[i]];
    }
    // Writing changed string s - our answer - into output file
    writing(s);

    // EXIT_SUCCESS is equivalent to 0 -> successfully finished our program
    return EXIT_SUCCESS;
}
