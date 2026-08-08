#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <sys/stat.h>

#define BYPASS_PATH "/proc/mtk_battery_cmd/current_cmd"

int write_value(const char* value, int lock) {
    if (access(BYPASS_PATH, F_OK) != 0) {
        fprintf(stderr, "Error: Bypass route not found.\n");
        return -1;
    }

    chmod(BYPASS_PATH, 0644);

    FILE *f = fopen(BYPASS_PATH, "w");
    if (!f) return -1;

    fprintf(f, "%s\n", value);
    fclose(f);

    if (lock) {
        chmod(BYPASS_PATH, 0444);
    }
    return 0;
}

int main(int argc, char *argv[]) {
    if (getuid() != 0) {
        fprintf(stderr, "This executable requires root permission.\n");
        return 1;
    }

    if (argc != 2) return 1;

    if (strcmp(argv[1], "enable") == 0) {
        return write_value("0 1", 1);
    } else if (strcmp(argv[1], "disable") == 0) {
        return write_value("0 0", 0);
    }

    return 1;
}
