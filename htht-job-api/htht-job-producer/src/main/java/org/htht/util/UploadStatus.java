package org.htht.util;

/**
 * FTP�ϴ��ļ�ö����
 * 2011-7-18
 * @author Nobita
 */
public enum UploadStatus
{

	CREATE_DIRECTORY_FAIL, // Զ�̷�������ӦĿ¼����ʧ��


	CREATE_DIRECTORY_SUCCESS, // Զ�̷���������Ŀ¼�ɹ�


	UPLOAD_NEW_FILE_SUCCESS, // �ϴ����ļ��ɹ�


	UPLOAD_NEW_FILE_FAILED, // �ϴ����ļ�ʧ��


	FILE_EXITS, // �ļ��Ѿ�����


	REMOTE_BIGGER_LOCAL, // Զ���ļ����ڱ����ļ�


	UPLOAD_FROM_BREAK_SUCCESS, // �ϵ���ɹ�


	UPLOAD_FROM_BREAK_FAILED, // �ϵ���ʧ��


	DELETE_REMOTE_FAILD; // ɾ��Զ���ļ�ʧ��

}