/*
 * Copyright 2013 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.terasology.math;

import org.lwjgl.BufferUtils;
import org.terasology.world.chunks.Chunk;

import javax.vecmath.Matrix3f;
import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;
import java.nio.FloatBuffer;

/**
 * Collection of math functions.
 *
 * @author Benjamin Glatzel <benjamin.glatzel@me.com>
 */
public final class TeraMath {

    public static final float PI = (float) Math.PI;
    public static final float RAD_TO_DEG = (float) (180.0f / Math.PI);
    public static final float DEG_TO_RAD = (float) (Math.PI / 180.0f);

    private TeraMath() {
    }

    /**
     * Returns the absolute value.
     *
     * @param i
     * @return the absolute value
     */
    public static int fastAbs(int i) {
        return (i >= 0) ? i : -i;
    }

    /**
     * Returns the absolute value (float variant)
     *
     * @param d
     * @return the absolute value
     */
    public static float fastAbs(float d) {
        return (d >= 0) ? d : -d;
    }

    /**
     * Returns the absolute value (double variant).
     *
     * @param d
     * @return the absolute value of d
     */
    public static double fastAbs(double d) {
        return (d >= 0) ? d : -d;
    }

    /**
     * Fast floor function
     *
     * @param d
     * @return
     */
    public static double fastFloor(double d) {
        int i = (int) d;
        return (d < 0 && d != i) ? i - 1 : i;
    }

    /**
     * Fast floor function
     *
     * @param d
     * @return
     */
    public static float fastFloor(float d) {
        int i = (int) d;
        return (d < 0 && d != i) ? i - 1 : i;
    }

    /**
     * Clamps a given value to be an element of [0..1].
     *
     * @param value
     * @return
     */
    public static double clamp(double value) {
        if (value > 1.0) {
            return 1.0;
        } else if (value < 0.0) {
            return 0.0;
        }
        return value;
    }

    /**
     * Clamps a given value to be an element of [min..max].
     *
     * @param value
     * @param min
     * @param max
     * @return
     */
    public static double clamp(double value, double min, double max) {
        if (value > max) {
            return max;
        } else if (value < min) {
            return min;
        }
        return value;
    }

    /**
     * Clamps a given value to be an element of [min..max].
     *
     * @param value
     * @param min
     * @param max
     * @return
     */
    public static float clamp(float value, float min, float max) {
        if (value > max) {
            return max;
        } else if (value < min) {
            return min;
        }
        return value;
    }

    /**
     * Clamps a given value to be an element of [min..max].
     *
     * @param value
     * @param min
     * @param max
     * @return
     */
    public static int clamp(int value, int min, int max) {
        if (value > max) {
            return max;
        } else if (value < min) {
            return min;
        }
        return value;
    }

    /**
     * Bilinear interpolation.
     */
    public static double biLerp(double x, double y, double q11, double q12, double q21, double q22, double x1, double x2, double y1, double y2) {
        double r1 = lerp(x, x1, x2, q11, q21);
        double r2 = lerp(x, x1, x2, q12, q22);
        return lerp(y, y1, y2, r1, r2);
    }

    /**
     * Linear interpolation.
     */
    public static double lerp(double x, double x1, double x2, double q00, double q01) {
        return ((x2 - x) / (x2 - x1)) * q00 + ((x - x1) / (x2 - x1)) * q01;
    }

    public static double lerp(double x1, double x2, double p) {
        return x1 * (1.0 - p) + x2 * p;
    }

    public static float lerpf(float x1, float x2, float p) {
        return x1 * (1.0f - p) + x2 * p;
    }

    /**
     * Trilinear interpolation.
     */
    public static double triLerp(double x, double y, double z, double q000, double q001, double q010, double q011, double q100, double q101, double q110, double q111,
                                 double x1, double x2, double y1, double y2, double z1, double z2) {
        double x00 = lerp(x, x1, x2, q000, q100);
        double x10 = lerp(x, x1, x2, q010, q110);
        double x01 = lerp(x, x1, x2, q001, q101);
        double x11 = lerp(x, x1, x2, q011, q111);
        double r0 = lerp(y, y1, y2, x00, x01);
        double r1 = lerp(y, y1, y2, x10, x11);
        return lerp(z, z1, z2, r0, r1);
    }

    /**
     * Maps any given value to be positive only.
     */
    public static int mapToPositive(int x) {
        return (x >= 0) ? x * 2 : -x * 2 - 1;
    }

    /**
     * Recreates the original value after applying "mapToPositive".
     */
    public static int redoMapToPositive(int x) {
        return (x % 2 == 0) ? x / 2 : -(x / 2) - 1;
    }

    /**
     * Applies Cantor's pairing function to 2D coordinates.
     *
     * @param k1 X-coordinate
     * @param k2 Y-coordinate
     * @return Unique 1D value
     */
    public static int cantorize(int k1, int k2) {
        return ((k1 + k2) * (k1 + k2 + 1) / 2) + k2;
    }

    /**
     * Inverse function of Cantor's pairing function.
     *
     * @param c Cantor value
     * @return Value along the x-axis
     */
    public static int cantorX(int c) {
        int j = (int) (java.lang.Math.sqrt(0.25 + 2 * c) - 0.5);
        return j - cantorY(c);
    }

    /**
     * Inverse function of Cantor's pairing function.
     *
     * @param c Cantor value
     * @return Value along the y-axis
     */
    public static int cantorY(int c) {
        int j = (int) (java.lang.Math.sqrt(0.25 + 2 * c) - 0.5);
        return c - j * (j + 1) / 2;
    }

    /**
     * Returns the chunk position of a given coordinate.
     *
     * @param x The X-coordinate of the block
     * @return The X-coordinate of the chunk
     */
    public static int calcChunkPosX(int x, int chunkPowerX) {
        return (x >> chunkPowerX);
    }

    public static int calcChunkPosX(int x) {
        return calcChunkPosX(x, Chunk.POWER_X);
    }

    /**
     * Returns the chunk position of a given coordinate
     *
     * @param y
     * @return The Y-coordinate of the chunk
     */
    public static int calcChunkPosY(int y) {
        return calcChunkPosY(y, Chunk.POWER_Y);
    }

    /**
    * Returns the chunk position of a given coordinate.
    *
    * @param y The Y-coordinate of the block
    * @return The Y-coordinate of the chunk
    */
    public static int calcChunkPosY(int y, int chunkPowerY) {
        return (y >> chunkPowerY);
    }

    /**
     * Returns the chunk position of a given coordinate.
     *
     * @param z The Z-coordinate of the block
     * @return The Z-coordinate of the chunk
     */
    public static int calcChunkPosZ(int z, int chunkPowerZ) {
        return (z >> chunkPowerZ);
    }

    /**
     * Returns the chunk position of a given coordinate
     *
     * @param z The Z-coordinate of the block
     * @return The Z-coordinate of the chunk
     */
    public static int calcChunkPosZ(int z) {
        return calcChunkPosZ(z, Chunk.POWER_Z);
    }

    public static Vector3i calcChunkPos(Vector3i pos, Vector3i chunkPower) {
        return calcChunkPos(pos.x, pos.y, pos.z, chunkPower);
    }

    public static Vector3i calcChunkPos(Vector3i pos) {
        return calcChunkPos(pos.x, pos.y, pos.z);
    }

    public static Vector3i calcChunkPos(int x, int y, int z) {
        return calcChunkPos(x, y, z, Chunk.CHUNK_POWER);
    }

    public static Vector3i calcChunkPos(int x, int y, int z, Vector3i chunkPower) {
        return new Vector3i(calcChunkPosX(x, chunkPower.x), calcChunkPosY(y, chunkPower.y), calcChunkPosZ(z, chunkPower.z));
    }

    /**
     * Returns the internal position of a block within a chunk.
     *
     * @param blockX The X-coordinate of the block in the world
     * @return The X-coordinate of the block within the chunk
     */
    public static int calcBlockPosX(int blockX, int chunkPosFilterX) {
        return blockX & chunkPosFilterX;
    }


    public static int calcBlockPosX(int blockX) {
        return calcBlockPosX(blockX, Chunk.INNER_CHUNK_POS_FILTER_X);
    }

    public static int calcBlockPosY(int blockY) {
        return blockY;
    }

    /**
     * Returns the internal position of a block within a chunk.
     *
     * @param blockZ The Z-coordinate of the block in the world
     * @return The Z-coordinate of the block within the chunk
     */
    public static int calcBlockPosZ(int blockZ, int chunkPosFilterZ) {
        return blockZ & chunkPosFilterZ;
    }

    public static int calcBlockPosZ(int blockZ) {
        return calcBlockPosZ(blockZ, Chunk.INNER_CHUNK_POS_FILTER_Z);
    }

    public static Vector3i calcBlockPos(Vector3i worldPos) {
        return calcBlockPos(worldPos.x, worldPos.y, worldPos.z, Chunk.INNER_CHUNK_POS_FILTER);
    }

    public static Vector3i calcBlockPos(int x, int y, int z) {
        return calcBlockPos(x, y, z, Chunk.INNER_CHUNK_POS_FILTER);
    }

    public static Vector3i calcBlockPos(int x, int y, int z, Vector3i chunkFilterSize) {
        return new Vector3i(calcBlockPosX(x, chunkFilterSize.x), calcBlockPosY(y), calcBlockPosZ(z, chunkFilterSize.z));
    }

    public static Region3i getChunkRegionAroundWorldPos(Vector3i pos, int extent) {
        Vector3i minPos = new Vector3i(-extent, 0, -extent);
        minPos.add(pos);
        Vector3i maxPos = new Vector3i(extent, 0, extent);
        maxPos.add(pos);

        Vector3i minChunk = TeraMath.calcChunkPos(minPos);
        Vector3i maxChunk = TeraMath.calcChunkPos(maxPos);

        return Region3i.createFromMinMax(minChunk, maxChunk);
    }

    /**
     * Lowest power of two greater or equal to val
     * <p/>
     * For values &lt;= 0 returns 0
     *
     * @param val
     * @return The lowest power of two greater or equal to val
     */
    public static int ceilPowerOfTwo(int val) {
        int result = val - 1;
        result = (result >> 1) | result;
        result = (result >> 2) | result;
        result = (result >> 4) | result;
        result = (result >> 8) | result;
        result = (result >> 16) | result;
        result++;
        return result;
    }

    /**
     * @param val
     * @return Whether val is a power of two
     */
    public static boolean isPowerOfTwo(int val) {
        return val == ceilPowerOfTwo(val);
    }

    /**
     * @param value
     * @return The size of a power of two - that is, the exponent.
     */
    public static int sizeOfPower(int value) {
        int power = 0;
        int val = value;
        while (val > 1) {
            val = val >> 1;
            power++;
        }
        return power;
    }

    public static int floorToInt(float val) {
        int i = (int) val;
        return (val < 0 && val != i) ? i - 1 : i;
    }

    public static int ceilToInt(float val) {
        int i = (int) val;
        return (val >= 0 && val != i) ? i + 1 : i;
    }

    // TODO: Move to a matrix util class

    /**
     * Copies the given matrix into a newly allocated FloatBuffer.
     * The order of the elements is column major (as used by OpenGL).
     *
     * @param m the matrix to copy
     * @return A new FloatBuffer containing the matrix in column-major form.
     */
    public static FloatBuffer matrixToFloatBuffer(Matrix4f m) {
        FloatBuffer buffer = BufferUtils.createFloatBuffer(16);
        matrixToFloatBuffer(m, buffer);
        return buffer;
    }

    /**
     * Copies the given matrix into a newly allocated FloatBuffer.
     * The order of the elements is column major (as used by OpenGL).
     *
     * @param m the matrix to copy
     * @return A new FloatBuffer containing the matrix in column-major form.
     */
    public static FloatBuffer matrixToFloatBuffer(Matrix3f m) {
        FloatBuffer buffer = BufferUtils.createFloatBuffer(9);
        matrixToFloatBuffer(m, buffer);
        return buffer;
    }

    /**
     * Copies the given matrix into an existing FloatBuffer.
     * The order of the elements is column major (as used by OpenGL).
     *
     * @param m  the matrix to copy
     * @param fb the float buffer to copy the matrix into
     * @return The provided float buffer.
     */
    public static FloatBuffer matrixToFloatBuffer(Matrix3f m, FloatBuffer fb) {
        fb.put(m.m00);
        fb.put(m.m10);
        fb.put(m.m20);
        fb.put(m.m01);
        fb.put(m.m11);
        fb.put(m.m21);
        fb.put(m.m02);
        fb.put(m.m12);
        fb.put(m.m22);

        fb.flip();
        return fb;
    }

    /**
     * Copies the given matrix into an existing FloatBuffer.
     * The order of the elements is column major (as used by OpenGL).
     *
     * @param m  the matrix to copy
     * @param fb the float buffer to copy the matrix into
     * @return The provided float buffer.
     */
    public static FloatBuffer matrixToFloatBuffer(Matrix4f m, FloatBuffer fb) {
        fb.put(m.m00);
        fb.put(m.m10);
        fb.put(m.m20);
        fb.put(m.m30);
        fb.put(m.m01);
        fb.put(m.m11);
        fb.put(m.m21);
        fb.put(m.m31);
        fb.put(m.m02);
        fb.put(m.m12);
        fb.put(m.m22);
        fb.put(m.m32);
        fb.put(m.m03);
        fb.put(m.m13);
        fb.put(m.m23);
        fb.put(m.m33);

        fb.flip();
        return fb;
    }

    public static Matrix4f createViewMatrix(float eyeX, float eyeY, float eyeZ, float centerX, float centerY, float centerZ, float upX, float upY, float upZ) {
        return createViewMatrix(new Vector3f(eyeX, eyeY, eyeZ), new Vector3f(centerX, centerY, centerZ), new Vector3f(upX, upY, upZ));
    }

    public static Matrix4f createViewMatrix(Vector3f eye, Vector3f center, Vector3f up) {
        Matrix4f m = new Matrix4f();

        Vector3f f = new Vector3f();
        f.sub(center, eye);

        f.normalize();
        up.normalize();

        Vector3f s = new Vector3f();
        s.cross(f, up);
        s.normalize();

        Vector3f u = new Vector3f();
        u.cross(s, f);
        u.normalize();

        m.m00 = s.x;
        m.m10 = s.y;
        m.m20 = s.z;
        m.m30 = 0;
        m.m01 = u.x;
        m.m11 = u.y;
        m.m21 = u.z;
        m.m31 = 0;
        m.m02 = -f.x;
        m.m12 = -f.y;
        m.m22 = -f.z;
        m.m32 = 0;
        m.m03 = 0;
        m.m13 = 0;
        m.m23 = 0;
        m.m33 = 1;

        m.m30 = -eye.x;
        m.m31 = -eye.y;
        m.m32 = -eye.z;

        m.transpose();

        return m;
    }

    public static Matrix4f createOrthogonalProjectionMatrix(float left, float right, float top, float bottom, float near, float far) {
        Matrix4f m = new Matrix4f();

        float lateral = right - left;
        float vertical = top - bottom;
        float forward = far - near;
        float tx = -(right + left) / (right - left);
        float ty = -(top + bottom) / (top - bottom);
        float tz = -(far + near) / (far - near);

        m.m00 = 2.0f / lateral;
        m.m10 = 0.0f;
        m.m20 = 0.0f;
        m.m30 = tx;
        m.m01 = 0.0f;
        m.m11 = 2.0f / vertical;
        m.m21 = 0.0f;
        m.m31 = ty;
        m.m02 = 0.0f;
        m.m12 = 0.0f;
        m.m22 = -2.0f / forward;
        m.m32 = tz;
        m.m03 = 0.0f;
        m.m13 = 0.0f;
        m.m23 = 0.0f;
        m.m33 = 1.0f;

        m.transpose();

        return m;
    }

    public static Matrix4f createPerspectiveProjectionMatrix(float fovY, float aspectRatio, float zNear, float zFar) {
        Matrix4f m = new Matrix4f();

        float f = 1.0f / (float) Math.tan(fovY * 0.5f);

        m.m00 = f / aspectRatio;
        m.m10 = 0;
        m.m20 = 0;
        m.m30 = 0;
        m.m01 = 0;
        m.m11 = f;
        m.m21 = 0;
        m.m31 = 0;
        m.m02 = 0;
        m.m12 = 0;
        m.m22 = (zFar + zNear) / (zNear - zFar);
        m.m32 = (2 * zFar * zNear) / (zNear - zFar);
        m.m03 = 0;
        m.m13 = 0;
        m.m23 = -1;
        m.m33 = 0;

        m.transpose();

        return m;
    }

    public static Matrix4f calcViewProjectionMatrix(Matrix4f vm, Matrix4f p) {
        Matrix4f result = new Matrix4f();
        result.mul(p, vm);
        return result;
    }

    public static Matrix4f calcModelViewMatrix(Matrix4f m, Matrix4f vm) {
        Matrix4f result = new Matrix4f();
        result.mul(m, vm);
        return result;
    }

    public static Matrix3f calcNormalMatrix(Matrix4f mv) {
        Matrix3f result = new Matrix3f();
        result.m00 = mv.m00;
        result.m10 = mv.m10;
        result.m20 = mv.m20;
        result.m01 = mv.m01;
        result.m11 = mv.m11;
        result.m21 = mv.m21;
        result.m02 = mv.m02;
        result.m12 = mv.m12;
        result.m22 = mv.m22;

        result.invert();
        result.transpose();
        return result;
    }

    // TODO: This doesn't belong in this class, move it.
    public static Side getSecondaryPlacementDirection(Vector3f direction, Vector3f normal) {
        Side surfaceDir = Side.inDirection(normal);
        Vector3f attachDir = surfaceDir.reverse().getVector3i().toVector3f();
        Vector3f rawDirection = new Vector3f(direction);
        float dot = rawDirection.dot(attachDir);
        rawDirection.sub(new Vector3f(dot * attachDir.x, dot * attachDir.y, dot * attachDir.z));
        return Side.inDirection(rawDirection.x, rawDirection.y, rawDirection.z).reverse();
    }

    /**
     * Produces a region containing the region touching the side of the given region, both in and outside the region.
     * @param region
     * @param side
     * @return
     */
    public static Region3i getEdgeRegion(Region3i region, Side side) {
        Vector3i sideDir = side.getVector3i();
        Vector3i min = region.min();
        Vector3i max = region.max();
        Vector3i edgeMin = new Vector3i(min);
        Vector3i edgeMax = new Vector3i(max);
        if (sideDir.x < 0) {
            edgeMin.x = min.x;
            edgeMax.x = min.x;
        } else if (sideDir.x > 0) {
            edgeMin.x = max.x;
            edgeMax.x = max.x;
        } else if (sideDir.y < 0) {
            edgeMin.y = min.y;
            edgeMax.y = min.y;
        } else if (sideDir.y > 0) {
            edgeMin.y = max.y;
            edgeMax.y = max.y;
        } else if (sideDir.z < 0) {
            edgeMin.z = min.z;
            edgeMax.z = min.z;
        } else if (sideDir.z > 0) {
            edgeMin.z = max.z;
            edgeMax.z = max.z;
        }
        return Region3i.createFromMinMax(edgeMin, edgeMax);
    }
}
