package com.testing;

@SuppressWarnings("PointlessBooleanExpression")
public class test {

    public static void main(String[] args) {
        assert compareVersions("1.0.0", "1.0.0") == true;
        assert compareVersions("1.0.0", "1.0.1") == true;
        assert compareVersions("1.0.0", "1.1.0") == true;
        assert compareVersions("1.0.0", "2.0.0") == true;
        assert compareVersions("1.0.0", "0.0.0") == false;
        assert compareVersions("1.0.0", "2")     == true;
        assert compareVersions("1.0.0", "2.0")   == true;
        assert compareVersions("1.0.0", "2.0.0") == true;
        assert compareVersions("4.3.5.14", "4.3.6.0") == true;
        assert compareVersions("4.3.5.14", "4.3.6.1") == true;

        System.out.println("All tests passed!");
    }

    /**
     * Requires ver format to be int.int.int... (ints separated by periods)
     * @return If currentVer satisfies minVer
     */
    public static boolean compareVersions(String minVer, String currentVer) {
        // Use major, minor, and patch version logic to compare
        String[] minParts = minVer.split("\\.");
        String[] curParts = currentVer.split("\\."); // May be of different length

        // Compare versions in order of significance
        for (int i = 0; i < minParts.length; i++) {
            int min = Integer.parseInt(minParts[i]);
            int cur = i < curParts.length ? Integer.parseInt(curParts[i]) : 0;
            if (cur > min) { return true; }
            if (cur < min) { return false; }
        }

        // If we have reached this point, the versions were equal
        return true;
    }
}
