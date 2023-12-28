#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <ctype.h>


// defining enum with players' positions
enum roles {
    Goalkeeper,
    Defender,
    Midfielder,
    Forward
};

// defining structure for each player
struct players {
    int ID;
    char name[16];
    int role;
    int age;
    int goals;
};

// function, which checks if the value, defining player's characteristics, is not equal to commands
int input_adequacy(char string[100]) {
    if (strcmp(string, "Update") == 0 || strcmp(string, "Search") == 0 || strcmp(string, "Add") == 0 ||
        strcmp(string, "Delete") == 0) {
        return 0;
    }
    return 1;
}


int main() {
    // defining name of the command operator, name, age and other player's characteristics
    char command[100], name[100], role[100];
    int age, goals, ID;

    // defining the array of structures - "team of players"
    struct players team[1001];

    // filling all positions with -1 to make it easier to found\update\etc.
    for (int i = 0; i < 1001; i++) {
        team[i].ID = -1;
    }

    FILE *file = fopen("input.txt", "r");
    FILE *openfile = fopen("output.txt", "w");

    int flag = 1;
    // inifinite loop - it finishes after "Display" command
    while (flag) {

        //scanning command from file
        fscanf(file, "%s", command);

        if (strcmp(command, "Add") == 0) {


            fscanf(file, "%d", &ID);
            char IDButString[1000];
            itoa(ID, IDButString, 10);

            fscanf(file, "%s", name);

            fscanf(file, "%s", role);

            char AgeButString[1000];
            fscanf(file, "%s", AgeButString);
            int age = atoi(AgeButString);

            char GoalsButString[1000];
            fscanf(file, "%s", GoalsButString);
            int goals = atoi(GoalsButString);


            // cheching if all inputs != command name
            if (input_adequacy(IDButString) == 0 || input_adequacy(name) == 0 || input_adequacy(role) == 0
                || input_adequacy(AgeButString) == 0 || input_adequacy(GoalsButString) == 0) {
                fputs("Invalid inputs\n", openfile);
                fclose(openfile);
                return EXIT_SUCCESS;
            }

            // Checking if ID consists only of digits
            for (int i = 0; i < strlen(IDButString); i++) {
                if (!isdigit(IDButString[i])) {
                    fputs("Invalid inputs\n", openfile);
                    fclose(openfile);

                    return EXIT_SUCCESS;
                }
            }

            // Checking if ID is unique
            for (int i = 0; i < 1001; i++) {
                if (ID == team[i].ID) {
                    fputs("Invalid inputs\n", openfile);
                    fclose(openfile);
                    return EXIT_SUCCESS;
                }
            }

            // checking if name starts with capital letter
            if (isupper(name[0]) == 0) {
                fputs("Invalid inputs\n", openfile);
                fclose(openfile);
                return EXIT_SUCCESS;
            }

            // checking name length
            if (strlen(name) < 2 || strlen(name) > 15) {
                fputs("Invalid inputs\n", openfile);
                fclose(openfile);
                return EXIT_SUCCESS;
            }

            // checking if name consistc only of English letters
            for (int i = 0; i < strlen(name); i++) {
                if ((name[i] >= 'A' && name[i] <= 'Z') || (name[i] >= 'a' && name[i] <= 'z')) {
                    continue;
                } else {
                    fputs("Invalid inputs\n", openfile);
                    fclose(openfile);
                    return EXIT_SUCCESS;
                }
            }

            // checking if age consists only of digits
            for (int i = 0; i < strlen(AgeButString); i++) {
                if (!isdigit(AgeButString[i])) {
                    fputs("Invalid inputs\n", openfile);
                    fclose(openfile);
                    return EXIT_SUCCESS;
                }
            }

            // checking age's range
            if (age < 18 || age > 100) {
                fputs("Invalid inputs\n", openfile);
                fclose(openfile);
                return EXIT_SUCCESS;
            }

            // checking if goals consist only of digits
            for (int i = 0; i < strlen(GoalsButString); i++) {
                if (!isdigit(GoalsButString[i])) {
                    fputs("Invalid inputs\n", openfile);
                    fclose(openfile);
                }
            }

            // checking if goals consist only of digits
            if (goals < 0 || goals >= 1000) {
                fputs("Invalid inputs\n", openfile);
                fclose(openfile);
            }

            // going through the array of structures
            for (int i = 0; i < 1001; ++i) {
                if (team[i].ID == -1) {
                    // defining a role of player using enum
                    if (strcmp(role, "Goalkeeper") == 0) {
                        team[i].role = Goalkeeper;
                    } else if (strcmp(role, "Defender") == 0) {
                        team[i].role = Defender;
                    } else if (strcmp(role, "Midfielder") == 0) {
                        team[i].role = Midfielder;
                    } else if (strcmp(role, "Forward") == 0) {
                        team[i].role = Forward;
                    } else {
                        fputs("Invalid inputs\n", openfile);
                        fclose(openfile);
                        return EXIT_SUCCESS;
                    }

                    // filling all other fields of player's characteristic
                    team[i].ID = ID;
                    strcpy(team[i].name, name);
                    team[i].age = age;
                    team[i].goals = goals;
                    break;
                }
            }


        } else if (strcmp(command, "Update") == 0) {

            // "Update" command - all checks are the same as in "Add" command

            fscanf(file, "%d", &ID);
            char IDButString[1000];
            itoa(ID, IDButString, 10);

            fscanf(file, "%s", name);

            fscanf(file, "%s", role);

            char AgeButString[1000];
            fscanf(file, "%s", AgeButString);
            int age = atoi(AgeButString);

            char GoalsButString[1000];
            fscanf(file, "%s", GoalsButString);
            int goals = atoi(GoalsButString);

            if (input_adequacy(IDButString) == 0 || input_adequacy(name) == 0 || input_adequacy(role) == 0
                || input_adequacy(AgeButString) == 0 || input_adequacy(GoalsButString) == 0) {
                fputs("Invalid inputs\n", openfile);
                fclose(openfile);
                return EXIT_SUCCESS;
            }

            for (int i = 0; i < strlen(IDButString); i++) {
                if (!isdigit(IDButString[i])) {
                    fputs("Invalid inputs\n", openfile);
                    fclose(openfile);
                    return EXIT_SUCCESS;
                }
            }

            // Checking if ID is NOT unique - we need to change existing values
            int counter_of_uniqness = 0;
            for (int i = 0; i < 1001; i++) {
                if (ID == team[i].ID) {
                    counter_of_uniqness++;
                    break;
                }
            }
            if (counter_of_uniqness == 0) {
                fputs("Invalid inputs\n", openfile);
                fclose(openfile);
                return EXIT_SUCCESS;
            }

            if (!isupper(name[0])) {
                fputs("Invalid inputs\n", openfile);
                fclose(openfile);
                return EXIT_SUCCESS;
            }

            if (strlen(name) < 2 || strlen(name) > 15) {
                fputs("Invalid inputs\n", openfile);
                fclose(openfile);
                return EXIT_SUCCESS;
            }

            for (int i = 0; i < strlen(name); i++) {
                if ((name[i] >= 'A' && name[i] <= 'Z') || (name[i] >= 'a' && name[i] <= 'z')) {
                    continue;
                } else {
                    fputs("Invalid inputs\n", openfile);
                    fclose(openfile);
                    return EXIT_SUCCESS;
                }
            }

            for (int i = 0; i < strlen(AgeButString); i++) {
                if (!isdigit(AgeButString[i])) {
                    fputs("Invalid inputs\n", openfile);
                    fclose(openfile);
                    return EXIT_SUCCESS;
                }
            }

            if (age < 18 || age > 100) {
                fputs("Invalid inputs\n", openfile);
                fclose(openfile);
                return EXIT_SUCCESS;
            }

            for (int i = 0; i < strlen(GoalsButString); i++) {
                if (!isdigit(GoalsButString[i])) {
                    fputs("Invalid inputs\n", openfile);
                    fclose(openfile);
                    return EXIT_SUCCESS;
                }
            }

            if (goals < 0 || goals >= 1000) {
                fputs("Invalid inputs\n", openfile);
                fclose(openfile);
                return EXIT_SUCCESS;
            }

            // the same scheme as in "Add", but here we replace everything instead of ID
            for (int i = 0; i < 1001; ++i) {
                if (team[i].ID == ID) {
                    strcpy(team[i].name, name);

                    if (strcmp(role, "Goalkeeper") == 0) {
                        team[i].role = Goalkeeper;
                    } else if (strcmp(role, "Defender") == 0) {
                        team[i].role = Defender;
                    } else if (strcmp(role, "Midfielder") == 0) {
                        team[i].role = Midfielder;
                    } else if (strcmp(role, "Forward") == 0) {
                        team[i].role = Forward;
                    } else {
                        fputs("Invalid inputs\n", openfile);
                        fclose(openfile);
                        return EXIT_SUCCESS;
                    }
                    team[i].age = age;
                    team[i].goals = goals;
                    break;
                }
            }


        } else if (strcmp(command, "Delete") == 0) {

            // Here we need only ID
            fscanf(file, "%d", &ID);
            char IDButString[1000];
            itoa(ID, IDButString, 10);

            // Checking if ID consists only of digits
            for (int i = 0; i < strlen(IDButString); i++) {
                if (!isdigit(IDButString[i])) {
                    fputs("Invalid inputs\n", openfile);
                    fclose(openfile);
                    return EXIT_SUCCESS;
                }
            }

            // Checking if ID exists in the "team"
            int delete_checker = 0;

            for (int i = 0; i < 1001; i++) {
                if (ID == team[i].ID) {
                    delete_checker++;
                    team[i].ID = -1;
                    strcpy(team[i].name, "");
                    team[i].role = -1;
                    team[i].age = -1;
                    team[i].goals = -1;
                    break;
                }
            }

            if (delete_checker == 0) {
                fputs("Impossible to delete\n", openfile);
            }

        } else if (strcmp(command, "Search") == 0) {

            // Here we need only ID
            fscanf(file, "%d", &ID);
            char IDButString[1000];
            itoa(ID, IDButString, 10);

            // Checking if ID consists only of digits
            for (int i = 0; i < strlen(IDButString); i++) {
                if (!isdigit(IDButString[i])) {
                    fputs("Invalid inputs\n", openfile);
                    fclose(openfile);
                    return EXIT_SUCCESS;
                }
            }

            // checking if player with inputted ID exists
            int found_notfound = 0;
            for (int i = 0; i < 1001; i++) {
                if (team[i].ID == ID) {
                    found_notfound++;
                    fputs("Found\n", openfile);
                    break;
                }
            }
            if (found_notfound == 0) {
                fputs("Not found\n", openfile);
            }

        } else if (strcmp(command, "Display") == 0) {

            // Going through the whole array of structures and outputting only relevant existing players from team
            int counter_of_players = 0;
            for (int i = 0; i < 1001; ++i) {
                if (team[i].ID != -1) {

                    char string_display[50];
                    char role_current[100];

                    counter_of_players++;

                    if (team[i].role == Goalkeeper) {
                        strcpy(role_current, "Goalkeeper");
                    } else if (team[i].role == Defender) {
                        strcpy(role_current, "Defender");
                    } else if (team[i].role == Midfielder) {
                        strcpy(role_current, "Midfielder");
                    } else if (team[i].role == Forward) {
                        strcpy(role_current, "Forward");
                    }
                    sprintf(string_display, "ID: %d, Name: %s, Position: %s, Age: %d, Goals: %d\n",
                            team[i].ID, team[i].name, role_current, team[i].age, team[i].goals);
                    fputs(string_display, openfile);
                }
            }

            if (counter_of_players == 0) {
                fputs("Invalid inputs\n", openfile);
            }
            fclose(openfile);
            return EXIT_SUCCESS;
        } else {
            // if inputted line is not a command at all
            fputs("Invalid inputs\n", openfile);
            fclose(openfile);
            return EXIT_SUCCESS;
        }
    }

}
