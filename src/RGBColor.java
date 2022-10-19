public class RGBColor {
    private int[] colors;

    public RGBColor(int[] colorArray) {
        if (colorArray.length != 3) {
            throw new RuntimeException("Color array must be of size 3");
        }
        for (int colorValue : colorArray) {
            validateColorValue(colorValue);
        }
        colors = colorArray;
    }

    public RGBColor(int r, int g, int b) {
        int[] colorArray = new int[] {r,g,b};
        for (int i : colorArray) {
            if (i < 0 || i > 255) {
            }
        }
        colors = colorArray;
    }

    public int[] getColorArray() {
        return colors;
    }

    public int r() {
        return colors[0];
    }
    public int g() {
        return colors[1];
    }
    public int b() {
        return colors[2];
    }

    public void r(int r) {
        validateColorValue(r);
        colors[0] = r;
    }
    public void g(int g) {
        validateColorValue(g);
        colors[1] = g;
    }
    public void b(int b) {
        validateColorValue(b);
        colors[2] = b;
    }

    private void validateColorValue(int colorValue) throws RuntimeException {
        if (colorValue < 0 || colorValue > 255) {
            throw new RuntimeException("Color values must be between 0 and 255");
        }
    }
}
