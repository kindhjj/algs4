/* https://coursera.cs.princeton.edu/algs4/assignments/seam/specification.php
*  score: 100/100 */

import edu.princeton.cs.algs4.Picture;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.Stack;

public class SeamCarver {

    private int[] rgb;
    private double[] energy;
    private int width;
    private int hight;

    // create a seam carver object based on the given picture
    public SeamCarver(Picture picture) {
        if (picture == null) throw new IllegalArgumentException();
        Picture currentPic = new Picture(picture);
        width = currentPic.width();
        hight = currentPic.height();
        int totalPixels = width * hight;
        rgb = new int[totalPixels];
        for (int r = 0; r < hight; r++)
            for (int c = 0; c < width; c++) {
                int rgbn = currentPic.getRGB(c, r);
                int position = c + r * width;
                rgb[position] = rgbn;
            }
        calculateEnergy();
    }

    // current picture
    public Picture picture() {
        Picture currentPic = new Picture(width, hight);
        for (int r = 0; r < hight; r++)
            for (int c = 0; c < width; c++) {
                int position = c + r * width;
                currentPic.setRGB(c, r, rgb[position]);
            }
        return currentPic;
    }

    // width of current picture
    public int width() {
        return width;
    }

    // height of current picture
    public int height() {
        return hight;
    }

    // energy of pixel at column x and row y
    public double energy(int x, int y) {
        if (x < 0 || y < 0 || x >= width || y >= hight) throw new IllegalArgumentException();
        return energy[x + y * width];
    }

    // sequence of indices for horizontal seam
    public int[] findHorizontalSeam() {
        double[] distTo = new double[energy.length];
        int[] prevVertex = new int[energy.length];
        for (int position = 0; position < distTo.length; position++)
            distTo[position] = Double.POSITIVE_INFINITY;
        for (int c = 0; c < width; c++)
            for (int r = 0; r < hight; r++) {
                int position = c + r * width;
                if (c == 0) {
                    distTo[position] = 1000.0;
                    prevVertex[position] = -1;
                }
                if (c == width - 1)
                    relax(distTo, prevVertex, position, energy.length - 1);
                else {
                    relax(distTo, prevVertex, position, position + 1);
                    if (r > 0)
                        relax(distTo, prevVertex, position, position - width + 1);
                    if (r < hight - 1)
                        relax(distTo, prevVertex, position, position + width + 1);
                }
            }
        Stack<Integer> reversePath = new Stack<>();
        int[] path = new int[width];
        for (int position = energy.length - 1; position != -1; position = prevVertex[position])
            reversePath.push(position);
        assert reversePath.size() == width + 1;
        for (int i = 0; i < width; i++)
            path[i] = reversePath.pop() / width;
        return path;
    }

    // sequence of indices for vertical seam
    public int[] findVerticalSeam() {
        double[] distTo = new double[energy.length];
        int[] prevVertex = new int[energy.length];
        for (int position = 0; position < distTo.length; position++)
            distTo[position] = Double.POSITIVE_INFINITY;
        for (int position = 0; position < energy.length - 1; position++) {
            if (position < width) {
                distTo[position] = 1000.0;
                prevVertex[position] = -1;
            }
            if (position / width + 1 == hight)
                relax(distTo, prevVertex, position, energy.length - 1);
            else {
                relax(distTo, prevVertex, position, position + width);
                if (position % width != 0)
                    relax(distTo, prevVertex, position, position + width - 1);
                if ((position + 1) % width != 0)
                    relax(distTo, prevVertex, position, position + width + 1);
            }
        }
        Stack<Integer> reversePath = new Stack<>();
        int[] path = new int[hight];
        for (int position = energy.length - 1; position != -1; position = prevVertex[position])
            reversePath.push(position);
        assert reversePath.size() == hight + 1;
        for (int i = 0; i < hight; i++)
            path[i] = reversePath.pop() % width;
        return path;
    }

    // remove horizontal seam from current picture
    public void removeHorizontalSeam(int[] seam) {
        checkSeam(seam, hight - 1, width);
        int newHight = hight - 1;
        int newPixelBound = width * newHight;
        int[] newRGB = new int[newPixelBound];
        double[] newEnergy = new double[newPixelBound + 1];
        int seamCol = 0;
        Queue<Integer> recalEnergyQ = new Queue<>();
        for (int c = 0; c < width; c++) {
            for (int r = 0; r < hight; r++) {
                int position = c + r * width;
                if (r < seam[seamCol]) {
                    newRGB[position] = rgb[position];
                    newEnergy[position] = energy[position];
                }
                else if (r == seam[seamCol]) {
                    if (r > 0) recalEnergyQ.enqueue(position - width);
                }
                else {
                    int newPosition = c + (r - 1) * width;
                    newRGB[newPosition] = rgb[position];
                    newEnergy[newPosition] = energy[position];
                    if (r == seam[seamCol] + 1) recalEnergyQ.enqueue(newPosition);
                }
            }
            seamCol++;
        }
        while (recalEnergyQ.size() > 0) {
            int thisPosition = recalEnergyQ.dequeue();
            newEnergy[thisPosition] = calculateSingleEnergy(thisPosition, width, newHight, newRGB);
        }
        rgb = newRGB;
        energy = newEnergy;
        hight--;
    }

    // remove vertical seam from current picture
    public void removeVerticalSeam(int[] seam) {
        checkSeam(seam, width - 1, hight);
        int newWidth = width - 1;
        int newPixelBound = hight * newWidth;
        int[] newRGB = new int[newPixelBound];
        double[] newEnergy = new double[newPixelBound + 1];
        int seamRow = 0;
        Queue<Integer> recalEnergyQ = new Queue<>();
        for (int r = 0; r < hight; r++) {
            for (int c = 0; c < width; c++) {
                int position = c + r * width;
                int newPosition = c + r * newWidth;
                if (c < seam[seamRow]) {
                    newRGB[newPosition] = rgb[position];
                    newEnergy[newPosition] = energy[position];
                }
                else if (c == seam[seamRow]) {
                    if (c > 0) recalEnergyQ.enqueue(newPosition - 1);
                }
                else {
                    newPosition--;
                    newRGB[newPosition] = rgb[position];
                    newEnergy[newPosition] = energy[position];
                    if (c == seam[seamRow] + 1) recalEnergyQ.enqueue(newPosition);
                }
            }
            seamRow++;
        }
        while (recalEnergyQ.size() > 0) {
            int thisPosition = recalEnergyQ.dequeue();
            newEnergy[thisPosition] = calculateSingleEnergy(thisPosition, newWidth, hight, newRGB);
        }
        newEnergy[newPixelBound] = 0.0;
        rgb = newRGB;
        energy = newEnergy;
        width--;
    }

    // caculate energy and cache in the variable
    private void calculateEnergy() {
        int bound = width * hight;
        energy = new double[bound + 1];
        for (int position = 0; position < bound; position++) {
            energy[position] = calculateSingleEnergy(position, width, hight, rgb);
        }
        energy[bound] = 0.0;
    }

    // calculate single pixel energy from rgb int
    private double calculateSingleEnergy(int position, int w, int h, int[] rgbn) {
        int row = position / w;
        int col = position % w;
        if (row == 0 || col == 0 || row + 1 == h || col + 1 == w) return 1000.0;
        int[] rgbPlus1, rgbMinus1, rgbPlusW, rgbMinusW;
        rgbPlus1 = new int[] {
                (rgbn[position + 1] >> 16) & 0xFF, (rgbn[position + 1] >> 8) & 0xFF,
                (rgbn[position + 1] >> 0) & 0xFF
        };
        rgbMinus1 = new int[] {
                (rgbn[position - 1] >> 16) & 0xFF, (rgbn[position - 1] >> 8) & 0xFF,
                (rgbn[position - 1] >> 0) & 0xFF
        };
        rgbPlusW = new int[] {
                (rgbn[position + w] >> 16) & 0xFF, (rgbn[position + w] >> 8) & 0xFF,
                (rgbn[position + w] >> 0) & 0xFF
        };
        rgbMinusW = new int[] {
                (rgbn[position - w] >> 16) & 0xFF, (rgbn[position - w] >> 8) & 0xFF,
                (rgbn[position - w] >> 0) & 0xFF
        };
        double deltaXsquared =
                (rgbMinus1[0] - rgbPlus1[0]) * (rgbMinus1[0] - rgbPlus1[0])
                        + (rgbMinus1[1] - rgbPlus1[1]) * (rgbMinus1[1] - rgbPlus1[1])
                        + (rgbMinus1[2] - rgbPlus1[2]) * (rgbMinus1[2] - rgbPlus1[2]);
        double deltaYsquared =
                (rgbMinusW[0] - rgbPlusW[0]) * (rgbMinusW[0] - rgbPlusW[0])
                        + (rgbMinusW[1] - rgbPlusW[1]) * (rgbMinusW[1] - rgbPlusW[1])
                        + (rgbMinusW[2] - rgbPlusW[2]) * (rgbMinusW[2] - rgbPlusW[2]);
        return Math.sqrt(deltaXsquared + deltaYsquared);
    }

    // check seam argument
    private void checkSeam(int[] seam, int seamBound, int seamLength) {
        if (seam == null) throw new IllegalArgumentException();
        if (seam.length != seamLength) throw new IllegalArgumentException();
        for (int i = 0; i < seam.length; i++) {
            if (seam[i] < 0 || seam[i] > seamBound) throw new IllegalArgumentException();
            if (i < seam.length - 1 && Math.abs(seam[i] - seam[i + 1]) > 1)
                throw new IllegalArgumentException();
        }
    }

    // relax next vertex
    private void relax(double[] distTo, int[] prevVertex, int thisPos, int nextPos) {
        if (distTo[nextPos] > distTo[thisPos] + energy[nextPos]) {
            distTo[nextPos] = distTo[thisPos] + energy[nextPos];
            prevVertex[nextPos] = thisPos;
        }
    }

}