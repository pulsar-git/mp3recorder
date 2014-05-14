#include <jni.h>
#include <sys/types.h>
#include <unistd.h>
#include "lame.h"

lame_global_flags *gfp;
#define BUFF_SIZE	(4096)
#define TEMP_ARRAY_SIZE (BUFF_SIZE*2+7200)

JNIEXPORT jstring JNICALL Java_com_hilifes_record_LameWrapper_getVersion
  (JNIEnv * env, jclass c)
{
    char buffer[80];
    strncpy(buffer,get_lame_version(),80);
    return (*env)->NewStringUTF(env, buffer);
}

//1 44100 16K 3 9
JNIEXPORT jboolean JNICALL Java_com_hilifes_record_LameWrapper_Init(JNIEnv *env, jclass c,jint num_channel,jint samplerate,jint brate,jint mode,jint quality)
{
	gfp = lame_init();
	lame_set_num_channels(gfp,num_channel);
	lame_set_in_samplerate(gfp,samplerate);
	lame_set_brate(gfp,brate);
	lame_set_mode(gfp,mode);
	lame_set_quality(gfp,quality);	

	if(lame_init_params(gfp) == -1)
		return JNI_FALSE;
	
	return JNI_TRUE;
}


JNIEXPORT jbyteArray JNICALL Java_com_hilifes_record_LameWrapper_EncodeByte(JNIEnv *env, jclass c,jbyteArray audioInput, jint num_samples)
{
	int imp3;
	jbyteArray result;
	jbyte *element;
	short int * audio;
	unsigned char *stream_array;

	element = (*env)->GetByteArrayElements(env,audioInput,0); 
	audio = (short int *) element;

	stream_array = (unsigned char*) malloc((int)(num_samples)+7200);

	imp3 = lame_encode_buffer(gfp,audio,audio,num_samples/2,stream_array,TEMP_ARRAY_SIZE);

	(*env)->ReleaseByteArrayElements(env,audioInput,element,JNI_ABORT);

	result = (*env)->NewByteArray(env,imp3);
	if(result ==NULL)
		return NULL;

	(*env)->SetByteArrayRegion(env,result,0,imp3,stream_array);
	free(stream_array);

	return result;
}

JNIEXPORT jbyteArray JNICALL Java_com_hilifes_record_LameWrapper_EncodeShort(JNIEnv *env, jclass c,jshortArray audioInput, jint num_samples)
{
	int imp3;
	jbyteArray result;
	jshort *element;
	short int * audio;
	unsigned char *stream_array;

	element = (*env)->GetShortArrayElements(env,audioInput,0); 
	audio = (short int *) element;
	stream_array = (unsigned char*) malloc((int)(num_samples*2)+7200);

	imp3 = lame_encode_buffer(gfp,audio,audio,num_samples,stream_array,TEMP_ARRAY_SIZE);

	(*env)->ReleaseShortArrayElements(env,audioInput,element,JNI_ABORT);

	result = (*env)->NewByteArray(env,imp3);
	if(result ==NULL)
		return NULL;

	(*env)->SetByteArrayRegion(env,result,0,imp3,stream_array);
	free(stream_array);
	return result;
}

JNIEXPORT jbyteArray JNICALL Java_com_hilifes_record_LameWrapper_Flush(JNIEnv *env, jclass c)
{
	jbyteArray result;
	int imp3;
	unsigned char stream_array[20000];

	imp3=lame_encode_flush(gfp,stream_array,20000);

	result = (*env)->NewByteArray(env,imp3);
	if(result ==NULL)
		return NULL;
	(*env)->SetByteArrayRegion(env,result,0,imp3,stream_array);
	return result;
}

JNIEXPORT void JNICALL	Java_com_hilifes_record_LameWrapper_Close(JNIEnv *env, jclass c)
{
	lame_close(gfp);
}

