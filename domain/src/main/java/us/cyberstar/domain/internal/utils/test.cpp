void MatrixToQuaternion(std::vector<float> &quat, std::vector<float> &trans, std::vector<std::vector<float>> mat)
{
    quat.clear();
    quat = {0,0,0,0};
    trans.clear();
    trans = {0,0,0};
    trans[0] = mat[0][3];
    trans[1] = mat[1][3];
    trans[2] = mat[2][3];
    float  tr, s, q[4];
    int    i, j, k;
    int nxt[3] = {1, 2, 0};
    tr = mat[0][0] + mat[1][1] + mat[2][2];
    if (tr > 0.0)
    {
        s = sqrt (tr + 1.0);
        quat[3] = s / 2.0;
        s = 0.5 / s;
        quat[0] = (mat[1][2] - mat[2][1]) * s;
        quat[1] = (mat[2][0] - mat[0][2]) * s;
        quat[2] = (mat[0][1] - mat[1][0]) * s;
    }
    else
    {
        i = 0;
        if (mat[1][1] > mat[0][0]) i = 1;
        if (mat[2][2] > mat[i][i]) i = 2;
        j = nxt[i];
        k = nxt[j];
        s = sqrt ((mat[i][i] - (mat[j][j] + mat[k][k])) + 1.0);
        q[i] = s * 0.5;
        if (s != 0.0) s = 0.5 / s;
        q[3] = (mat[j][k] - mat[k][j]) * s;
        q[j] = (mat[i][j] + mat[j][i]) * s;
        q[k] = (mat[i][k] + mat[k][i]) * s;
        quat[0] = q[0];
        quat[1] = q[1];
        quat[2] = q[2];
        quat[3] = q[3];
    }
}
void QuaternionToMatrix(std::vector<std::vector<float>>& mat, std::vector<float> quat, std::vector<float> trans)
{
    mat.clear();
    for (int i=0;i<4;i++)
        mat.push_back({0,0,0,0});
    mat[0][3] = trans[0];
    mat[1][3] = trans[1];
    mat[2][3] = trans[2];
    mat[3][3] = 1;
    float wx, wy, wz, xx, yy, yz, xy, xz, zz, x2, y2, z2;
    x2 = 2*quat[0];
    y2 = 2*quat[1];
    z2 = 2*quat[2];
    xx = quat[0] * x2;   xy = quat[0] * y2;   xz = quat[0] * z2;
    yy = quat[1] * y2;   yz = quat[1] * z2;   zz = quat[2] * z2;
    wx = quat[3] * x2;   wy = quat[3] * y2;   wz = quat[3] * z2;
    mat[0][0]=1.0f-(yy+zz); mat[0][1]=xy-wz;        mat[0][2]=xz+wy;
    mat[1][0]=xy+wz;        mat[1][1]=1.0f-(xx+zz); mat[1][2]=yz-wx;
    mat[2][0]=xz-wy;        mat[2][1]=yz+wx;        mat[2][2]=1.0f-(xx+yy);
}