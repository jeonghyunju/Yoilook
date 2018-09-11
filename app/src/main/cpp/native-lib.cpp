#include <jni.h>
#include <string>
#include <opencv2/opencv.hpp>

using namespace cv;
using namespace std;

extern "C" {

int processToNegative(Mat img_inputs, Mat &img_output) {
    cvtColor(img_inputs, img_output, CV_RGB2GRAY);
    Mat srcImage = img_output;

    Mat_<uchar>images(srcImage);
    Mat_<uchar>destImage(srcImage.size());
    for(int y=0 ; y<images.rows ; y++) {
        for(int x=0 ; x<images.cols ; x++) {
            uchar u = images(y, x);
            destImage(y, x) = 255-u;
        }
    }
    img_output = destImage.clone();
    return (0);
}

JNIEXPORT void JNICALL
Java_com_example_hyunjujung_yoil_ImageFilterSet_loadImageWater(JNIEnv *env, jobject instance,
                                                               jstring imageFileName_, jlong imgs) {
    Mat &img_input_water = *(Mat *) imgs;
    const char *imageFileName = env->GetStringUTFChars(imageFileName_, 0);
    img_input_water = imread(imageFileName, IMREAD_COLOR);
}

JNIEXPORT jint JNICALL
Java_com_example_hyunjujung_yoil_ImageFilterSet_waterprocessing(JNIEnv *env, jobject instance,
                                                                jlong inputimages,
                                                                jlong outputwater) {

    Mat &img_input_waters = *(Mat *) inputimages;
    Mat &img_output_water = *(Mat *) outputwater;

    int conv = processToNegative(img_input_waters, img_output_water);
    int ret = (jint)conv;

    return ret;

}

JNIEXPORT void JNICALL
Java_com_example_hyunjujung_yoil_ImageFilterSet_imageprocessing(JNIEnv *env, jobject instance,
                                                                jlong inputImage,
                                                                jlong outputImageG,
                                                                jlong outputImageE) {

    Mat &img_input_gray = *(Mat *) inputImage;
    Mat &img_input_embos = *(Mat *) inputImage;
    Mat &img_output_Gray = *(Mat *) outputImageG;
    Mat &img_output_Embose = *(Mat *) outputImageE;

    cvtColor(img_input_gray, img_output_Gray, CV_RGB2GRAY);
    cvtColor(img_input_embos, img_output_Embose, CV_RGB2GRAY);
    blur(img_output_Embose, img_output_Embose, Size(5,5));
    Canny(img_output_Embose, img_output_Embose, 50, 150, 5);

}

JNIEXPORT void JNICALL
Java_com_example_hyunjujung_yoil_ImageFilterSet_loadImage(JNIEnv *env, jobject instance,
                                                          jstring imageFileName, jlong img) {
    Mat &img_input = *(Mat *) img;

    const char *nativeFileName = env->GetStringUTFChars(imageFileName, 0);
    img_input = imread(nativeFileName, IMREAD_COLOR);

}


}


