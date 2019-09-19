/*******************************************************************************
 * Copyright 2011 See AUTHORS file.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package com.cyber.math;

import java.io.Serializable;

/**
 * A 3x3 <a href="http://en.wikipedia.org/wiki/Row-major_order#Column-major_order">column major</a> matrix; useful for 2D
 * transforms.
 *
 * @author mzechner
 */
public class Matrix3 implements Serializable {
    private static final long serialVersionUID = 7907569533774959788L;
    public static final int M11 = 0;
    public static final int M12 = 3;
    public static final int M13 = 6;
    public static final int M21 = 1;
    public static final int M22 = 4;
    public static final int M23 = 7;
    public static final int M31 = 2;
    public static final int M32 = 5;
    public static final int M33 = 8;
    public float[] val = new float[9];
    private float[] tmp = new float[9];

    public Matrix3() {
        idt();
    }

    public Matrix3(Matrix3 matrix) {
        set(matrix);
    }

    /**
     * Constructs a matrix from the given float array. The array must have at least 9 elements; the first 9 will be copied.
     *
     * @param values The float array to copy. Remember that this matrix is in <a
     *               href="http://en.wikipedia.org/wiki/Row-major_order#Column-major_order">column major</a> order. (The float array is
     *               not modified.)
     */
    public Matrix3(float[] values) {
        this.set(values);
    }

    /**
     * Sets this matrix to the identity matrix
     *
     * @return This matrix for the purpose of chaining operations.
     */
    public Matrix3 idt() {
        float[] val = this.val;
        val[M11] = 1;
        val[M21] = 0;
        val[M31] = 0;
        val[M12] = 0;
        val[M22] = 1;
        val[M32] = 0;
        val[M13] = 0;
        val[M23] = 0;
        val[M33] = 1;
        return this;
    }

    /**
     * Postmultiplies this matrix with the provided matrix and stores the result in this matrix. For example:
     *
     * <pre>
     * A.mul(B) results in A := AB
     * </pre>
     *
     * @param m Matrix to multiply by.
     * @return This matrix for the purpose of chaining operations together.
     */
    public Matrix3 mul(Matrix3 m) {
        float[] val = this.val;

        float v00 = val[M11] * m.val[M11] + val[M12] * m.val[M21] + val[M13] * m.val[M31];
        float v01 = val[M11] * m.val[M12] + val[M12] * m.val[M22] + val[M13] * m.val[M32];
        float v02 = val[M11] * m.val[M13] + val[M12] * m.val[M23] + val[M13] * m.val[M33];

        float v10 = val[M21] * m.val[M11] + val[M22] * m.val[M21] + val[M23] * m.val[M31];
        float v11 = val[M21] * m.val[M12] + val[M22] * m.val[M22] + val[M23] * m.val[M32];
        float v12 = val[M21] * m.val[M13] + val[M22] * m.val[M23] + val[M23] * m.val[M33];

        float v20 = val[M31] * m.val[M11] + val[M32] * m.val[M21] + val[M33] * m.val[M31];
        float v21 = val[M31] * m.val[M12] + val[M32] * m.val[M22] + val[M33] * m.val[M32];
        float v22 = val[M31] * m.val[M13] + val[M32] * m.val[M23] + val[M33] * m.val[M33];

        val[M11] = v00;
        val[M21] = v10;
        val[M31] = v20;
        val[M12] = v01;
        val[M22] = v11;
        val[M32] = v21;
        val[M13] = v02;
        val[M23] = v12;
        val[M33] = v22;

        return this;
    }

    /**
     * Premultiplies this matrix with the provided matrix and stores the result in this matrix. For example:
     *
     * <pre>
     * A.mulLeft(B) results in A := BA
     * </pre>
     *
     * @param m The other Matrix to multiply by
     * @return This matrix for the purpose of chaining operations.
     */
    public Matrix3 mulLeft(Matrix3 m) {
        float[] val = this.val;

        float v00 = m.val[M11] * val[M11] + m.val[M12] * val[M21] + m.val[M13] * val[M31];
        float v01 = m.val[M11] * val[M12] + m.val[M12] * val[M22] + m.val[M13] * val[M32];
        float v02 = m.val[M11] * val[M13] + m.val[M12] * val[M23] + m.val[M13] * val[M33];

        float v10 = m.val[M21] * val[M11] + m.val[M22] * val[M21] + m.val[M23] * val[M31];
        float v11 = m.val[M21] * val[M12] + m.val[M22] * val[M22] + m.val[M23] * val[M32];
        float v12 = m.val[M21] * val[M13] + m.val[M22] * val[M23] + m.val[M23] * val[M33];

        float v20 = m.val[M31] * val[M11] + m.val[M32] * val[M21] + m.val[M33] * val[M31];
        float v21 = m.val[M31] * val[M12] + m.val[M32] * val[M22] + m.val[M33] * val[M32];
        float v22 = m.val[M31] * val[M13] + m.val[M32] * val[M23] + m.val[M33] * val[M33];

        val[M11] = v00;
        val[M21] = v10;
        val[M31] = v20;
        val[M12] = v01;
        val[M22] = v11;
        val[M32] = v21;
        val[M13] = v02;
        val[M23] = v12;
        val[M33] = v22;

        return this;
    }

    /**
     * Sets this matrix to a rotation matrix that will rotate any vector in counter-clockwise direction around the z-axis.
     *
     * @param degrees the angle in degrees.
     * @return This matrix for the purpose of chaining operations.
     */
    public Matrix3 setToRotation(float degrees) {
        return setToRotationRad(MathUtils.degreesToRadians * degrees);
    }

    /**
     * Sets this matrix to a rotation matrix that will rotate any vector in counter-clockwise direction around the z-axis.
     *
     * @param radians the angle in radians.
     * @return This matrix for the purpose of chaining operations.
     */
    public Matrix3 setToRotationRad(float radians) {
        float cos = (float) Math.cos(radians);
        float sin = (float) Math.sin(radians);
        float[] val = this.val;

        val[M11] = cos;
        val[M21] = sin;
        val[M31] = 0;

        val[M12] = -sin;
        val[M22] = cos;
        val[M32] = 0;

        val[M13] = 0;
        val[M23] = 0;
        val[M33] = 1;

        return this;
    }

    public Matrix3 setToRotation(Vector3 axis, float degrees) {
        return setToRotation(axis, MathUtils.cosDeg(degrees), MathUtils.sinDeg(degrees));
    }

    public Matrix3 setToRotation(Vector3 axis, float cos, float sin) {
        float[] val = this.val;
        float oc = 1.0f - cos;
        val[M11] = oc * axis.x * axis.x + cos;
        val[M21] = oc * axis.x * axis.y - axis.z * sin;
        val[M31] = oc * axis.z * axis.x + axis.y * sin;
        val[M12] = oc * axis.x * axis.y + axis.z * sin;
        val[M22] = oc * axis.y * axis.y + cos;
        val[M32] = oc * axis.y * axis.z - axis.x * sin;
        val[M13] = oc * axis.z * axis.x - axis.y * sin;
        val[M23] = oc * axis.y * axis.z + axis.x * sin;
        val[M33] = oc * axis.z * axis.z + cos;
        return this;
    }

    /**
     * Sets this matrix to a translation matrix.
     *
     * @param x the translation in x
     * @param y the translation in y
     * @return This matrix for the purpose of chaining operations.
     */
    public Matrix3 setToTranslation(float x, float y) {
        float[] val = this.val;

        val[M11] = 1;
        val[M21] = 0;
        val[M31] = 0;

        val[M12] = 0;
        val[M22] = 1;
        val[M32] = 0;

        val[M13] = x;
        val[M23] = y;
        val[M33] = 1;

        return this;
    }

    /**
     * Sets this matrix to a translation matrix.
     *
     * @param translation The translation vector.
     * @return This matrix for the purpose of chaining operations.
     */
    public Matrix3 setToTranslation(Vector2 translation) {
        float[] val = this.val;

        val[M11] = 1;
        val[M21] = 0;
        val[M31] = 0;

        val[M12] = 0;
        val[M22] = 1;
        val[M32] = 0;

        val[M13] = translation.x;
        val[M23] = translation.y;
        val[M33] = 1;

        return this;
    }

    /**
     * Sets this matrix to a scaling matrix.
     *
     * @param scaleX the scale in x
     * @param scaleY the scale in y
     * @return This matrix for the purpose of chaining operations.
     */
    public Matrix3 setToScaling(float scaleX, float scaleY) {
        float[] val = this.val;
        val[M11] = scaleX;
        val[M21] = 0;
        val[M31] = 0;
        val[M12] = 0;
        val[M22] = scaleY;
        val[M32] = 0;
        val[M13] = 0;
        val[M23] = 0;
        val[M33] = 1;
        return this;
    }

    /**
     * Sets this matrix to a scaling matrix.
     *
     * @param scale The scale vector.
     * @return This matrix for the purpose of chaining operations.
     */
    public Matrix3 setToScaling(Vector2 scale) {
        float[] val = this.val;
        val[M11] = scale.x;
        val[M21] = 0;
        val[M31] = 0;
        val[M12] = 0;
        val[M22] = scale.y;
        val[M32] = 0;
        val[M13] = 0;
        val[M23] = 0;
        val[M33] = 1;
        return this;
    }

    public String toString() {
        float[] val = this.val;
        return "[" + val[M11] + "|" + val[M12] + "|" + val[M13] + "]\n" //
                + "[" + val[M21] + "|" + val[M22] + "|" + val[M23] + "]\n" //
                + "[" + val[M31] + "|" + val[M32] + "|" + val[M33] + "]";
    }

    /**
     * @return The determinant of this matrix
     */
    public float det() {
        float[] val = this.val;
        return val[M11] * val[M22] * val[M33] + val[M12] * val[M23] * val[M31] + val[M13] * val[M21] * val[M32] - val[M11]
                * val[M23] * val[M32] - val[M12] * val[M21] * val[M33] - val[M13] * val[M22] * val[M31];
    }

    /**
     * Inverts this matrix given that the determinant is != 0.
     *
     * @return This matrix for the purpose of chaining operations.
     * @throws Exception if the matrix is singular (not invertible)
     */
    public Matrix3 inv() throws Exception {
        float det = det();
        if (det == 0) throw new Exception("Can't invert a singular matrix");

        float inv_det = 1.0f / det;
        float[] tmp = this.tmp, val = this.val;

        tmp[M11] = val[M22] * val[M33] - val[M32] * val[M23];
        tmp[M21] = val[M31] * val[M23] - val[M21] * val[M33];
        tmp[M31] = val[M21] * val[M32] - val[M31] * val[M22];
        tmp[M12] = val[M32] * val[M13] - val[M12] * val[M33];
        tmp[M22] = val[M11] * val[M33] - val[M31] * val[M13];
        tmp[M32] = val[M31] * val[M12] - val[M11] * val[M32];
        tmp[M13] = val[M12] * val[M23] - val[M22] * val[M13];
        tmp[M23] = val[M21] * val[M13] - val[M11] * val[M23];
        tmp[M33] = val[M11] * val[M22] - val[M21] * val[M12];

        val[M11] = inv_det * tmp[M11];
        val[M21] = inv_det * tmp[M21];
        val[M31] = inv_det * tmp[M31];
        val[M12] = inv_det * tmp[M12];
        val[M22] = inv_det * tmp[M22];
        val[M32] = inv_det * tmp[M32];
        val[M13] = inv_det * tmp[M13];
        val[M23] = inv_det * tmp[M23];
        val[M33] = inv_det * tmp[M33];

        return this;
    }

    /**
     * Copies the values from the provided matrix to this matrix.
     *
     * @param mat The matrix to copy.
     * @return This matrix for the purposes of chaining.
     */
    public Matrix3 set(Matrix3 mat) {
        System.arraycopy(mat.val, 0, val, 0, val.length);
        return this;
    }


    /**
     * Sets this 3x3 matrix to the top left 3x3 corner of the provided 4x4 matrix.
     *
     * @param mat The matrix whose top left corner will be copied. This matrix will not be modified.
     * @return This matrix for the purpose of chaining operations.
     */
    public Matrix3 set(Matrix4 mat) {
        float[] val = this.val;
        val[M11] = mat.val[Matrix4.M11];
        val[M21] = mat.val[Matrix4.M21];
        val[M31] = mat.val[Matrix4.M31];
        val[M12] = mat.val[Matrix4.M12];
        val[M22] = mat.val[Matrix4.M22];
        val[M32] = mat.val[Matrix4.M32];
        val[M13] = mat.val[Matrix4.M13];
        val[M23] = mat.val[Matrix4.M23];
        val[M33] = mat.val[Matrix4.M33];
        return this;
    }

    /**
     * Sets the matrix to the given matrix as a float array. The float array must have at least 9 elements; the first 9 will be
     * copied.
     *
     * @param values The matrix, in float form, that is to be copied. Remember that this matrix is in <a
     *               href="http://en.wikipedia.org/wiki/Row-major_order#Column-major_order">column major</a> order.
     * @return This matrix for the purpose of chaining methods together.
     */
    public Matrix3 set(float[] values) {
        System.arraycopy(values, 0, val, 0, val.length);
        return this;
    }

    /**
     * Adds a translational component to the matrix in the 3rd column. The other columns are untouched.
     *
     * @param vector The translation vector.
     * @return This matrix for the purpose of chaining.
     */
    public Matrix3 trn(Vector2 vector) {
        val[M13] += vector.x;
        val[M23] += vector.y;
        return this;
    }

    /**
     * Adds a translational component to the matrix in the 3rd column. The other columns are untouched.
     *
     * @param x The x-component of the translation vector.
     * @param y The y-component of the translation vector.
     * @return This matrix for the purpose of chaining.
     */
    public Matrix3 trn(float x, float y) {
        val[M13] += x;
        val[M23] += y;
        return this;
    }

    /**
     * Adds a translational component to the matrix in the 3rd column. The other columns are untouched.
     *
     * @param vector The translation vector. (The z-component of the vector is ignored because this is a 3x3 matrix)
     * @return This matrix for the purpose of chaining.
     */
    public Matrix3 trn(Vector3 vector) {
        val[M13] += vector.x;
        val[M23] += vector.y;
        return this;
    }

    /**
     * Postmultiplies this matrix by a translation matrix. Postmultiplication is also used by OpenGL ES' 1.x
     * glTranslate/glRotate/glScale.
     *
     * @param x The x-component of the translation vector.
     * @param y The y-component of the translation vector.
     * @return This matrix for the purpose of chaining.
     */
    public Matrix3 translate(float x, float y) {
        float[] val = this.val;
        tmp[M11] = 1;
        tmp[M21] = 0;
        tmp[M31] = 0;

        tmp[M12] = 0;
        tmp[M22] = 1;
        tmp[M32] = 0;

        tmp[M13] = x;
        tmp[M23] = y;
        tmp[M33] = 1;
        mul(val, tmp);
        return this;
    }

    /**
     * Postmultiplies this matrix by a translation matrix. Postmultiplication is also used by OpenGL ES' 1.x
     * glTranslate/glRotate/glScale.
     *
     * @param translation The translation vector.
     * @return This matrix for the purpose of chaining.
     */
    public Matrix3 translate(Vector2 translation) {
        float[] val = this.val;
        tmp[M11] = 1;
        tmp[M21] = 0;
        tmp[M31] = 0;

        tmp[M12] = 0;
        tmp[M22] = 1;
        tmp[M32] = 0;

        tmp[M13] = translation.x;
        tmp[M23] = translation.y;
        tmp[M33] = 1;
        mul(val, tmp);
        return this;
    }

    /**
     * Postmultiplies this matrix with a (counter-clockwise) rotation matrix. Postmultiplication is also used by OpenGL ES' 1.x
     * glTranslate/glRotate/glScale.
     *
     * @param degrees The angle in degrees
     * @return This matrix for the purpose of chaining.
     */
    public Matrix3 rotate(float degrees) {
        return rotateRad(MathUtils.degreesToRadians * degrees);
    }

    /**
     * Postmultiplies this matrix with a (counter-clockwise) rotation matrix. Postmultiplication is also used by OpenGL ES' 1.x
     * glTranslate/glRotate/glScale.
     *
     * @param radians The angle in radians
     * @return This matrix for the purpose of chaining.
     */
    public Matrix3 rotateRad(float radians) {
        if (radians == 0) return this;
        float cos = (float) Math.cos(radians);
        float sin = (float) Math.sin(radians);
        float[] tmp = this.tmp;

        tmp[M11] = cos;
        tmp[M21] = sin;
        tmp[M31] = 0;

        tmp[M12] = -sin;
        tmp[M22] = cos;
        tmp[M32] = 0;

        tmp[M13] = 0;
        tmp[M23] = 0;
        tmp[M33] = 1;
        mul(val, tmp);
        return this;
    }

    /**
     * Postmultiplies this matrix with a scale matrix. Postmultiplication is also used by OpenGL ES' 1.x
     * glTranslate/glRotate/glScale.
     *
     * @param scaleX The scale in the x-axis.
     * @param scaleY The scale in the y-axis.
     * @return This matrix for the purpose of chaining.
     */
    public Matrix3 scale(float scaleX, float scaleY) {
        float[] tmp = this.tmp;
        tmp[M11] = scaleX;
        tmp[M21] = 0;
        tmp[M31] = 0;
        tmp[M12] = 0;
        tmp[M22] = scaleY;
        tmp[M32] = 0;
        tmp[M13] = 0;
        tmp[M23] = 0;
        tmp[M33] = 1;
        mul(val, tmp);
        return this;
    }

    /**
     * Postmultiplies this matrix with a scale matrix. Postmultiplication is also used by OpenGL ES' 1.x
     * glTranslate/glRotate/glScale.
     *
     * @param scale The vector to scale the matrix by.
     * @return This matrix for the purpose of chaining.
     */
    public Matrix3 scale(Vector2 scale) {
        float[] tmp = this.tmp;
        tmp[M11] = scale.x;
        tmp[M21] = 0;
        tmp[M31] = 0;
        tmp[M12] = 0;
        tmp[M22] = scale.y;
        tmp[M32] = 0;
        tmp[M13] = 0;
        tmp[M23] = 0;
        tmp[M33] = 1;
        mul(val, tmp);
        return this;
    }

    /**
     * Get the values in this matrix.
     *
     * @return The float values that make up this matrix in column-major order.
     */
    public float[] getValues() {
        return val;
    }

    public Vector2 getTranslation(Vector2 position) {
        position.x = val[M13];
        position.y = val[M23];
        return position;
    }

    public Vector2 getScale(Vector2 scale) {
        float[] val = this.val;
        scale.x = (float) Math.sqrt(val[M11] * val[M11] + val[M12] * val[M12]);
        scale.y = (float) Math.sqrt(val[M21] * val[M21] + val[M22] * val[M22]);
        return scale;
    }

    public float getRotation() {
        return MathUtils.radiansToDegrees * (float) Math.atan2(val[M21], val[M11]);
    }

    public float getRotationRad() {
        return (float) Math.atan2(val[M21], val[M11]);
    }

    /**
     * Scale the matrix in the both the x and y components by the scalar value.
     *
     * @param scale The single value that will be used to scale both the x and y components.
     * @return This matrix for the purpose of chaining methods together.
     */
    public Matrix3 scl(float scale) {
        val[M11] *= scale;
        val[M22] *= scale;
        return this;
    }

    /**
     * Scale this matrix using the x and y components of the vector but leave the rest of the matrix alone.
     *
     * @param scale The {@link Vector3} to use to scale this matrix.
     * @return This matrix for the purpose of chaining methods together.
     */
    public Matrix3 scl(Vector2 scale) {
        val[M11] *= scale.x;
        val[M22] *= scale.y;
        return this;
    }

    /**
     * Scale this matrix using the x and y components of the vector but leave the rest of the matrix alone.
     *
     * @param scale The {@link Vector3} to use to scale this matrix. The z component will be ignored.
     * @return This matrix for the purpose of chaining methods together.
     */
    public Matrix3 scl(Vector3 scale) {
        val[M11] *= scale.x;
        val[M22] *= scale.y;
        return this;
    }

    /**
     * Transposes the current matrix.
     *
     * @return This matrix for the purpose of chaining methods together.
     */
    public Matrix3 transpose() {
        // Where MXY you do not have to change MXX
        float[] val = this.val;
        float v01 = val[M21];
        float v02 = val[M31];
        float v10 = val[M12];
        float v12 = val[M32];
        float v20 = val[M13];
        float v21 = val[M23];
        val[M12] = v01;
        val[M13] = v02;
        val[M21] = v10;
        val[M23] = v12;
        val[M31] = v20;
        val[M32] = v21;
        return this;
    }

    /**
     * Multiplies matrix a with matrix b in the following manner:
     *
     * <pre>
     * mul(A, B) => A := AB
     * </pre>
     *
     * @param mata The float array representing the first matrix. Must have at least 9 elements.
     * @param matb The float array representing the second matrix. Must have at least 9 elements.
     */
    private static void mul(float[] mata, float[] matb) {
        float v00 = mata[M11] * matb[M11] + mata[M12] * matb[M21] + mata[M13] * matb[M31];
        float v01 = mata[M11] * matb[M12] + mata[M12] * matb[M22] + mata[M13] * matb[M32];
        float v02 = mata[M11] * matb[M13] + mata[M12] * matb[M23] + mata[M13] * matb[M33];

        float v10 = mata[M21] * matb[M11] + mata[M22] * matb[M21] + mata[M23] * matb[M31];
        float v11 = mata[M21] * matb[M12] + mata[M22] * matb[M22] + mata[M23] * matb[M32];
        float v12 = mata[M21] * matb[M13] + mata[M22] * matb[M23] + mata[M23] * matb[M33];

        float v20 = mata[M31] * matb[M11] + mata[M32] * matb[M21] + mata[M33] * matb[M31];
        float v21 = mata[M31] * matb[M12] + mata[M32] * matb[M22] + mata[M33] * matb[M32];
        float v22 = mata[M31] * matb[M13] + mata[M32] * matb[M23] + mata[M33] * matb[M33];

        mata[M11] = v00;
        mata[M21] = v10;
        mata[M31] = v20;
        mata[M12] = v01;
        mata[M22] = v11;
        mata[M32] = v21;
        mata[M13] = v02;
        mata[M23] = v12;
        mata[M33] = v22;
    }

    public Vector3 mul(Vector3 right) {
        Vector3 res = new Vector3();
        res.x = right.x * val[M11] + right.y * val[M12] + right.z * val[M13];
        res.y = right.x * val[M21] + right.y * val[M22] + right.z * val[M23];
        res.z = right.x * val[M31] + right.y * val[M32] + right.z * val[M33];
        return res;
    }
}
