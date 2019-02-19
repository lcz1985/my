package com.li.cson;

import java.io.IOException;

class HEAD {
	private boolean _compressed;
    private byte _protoVersion;
    public boolean getCompressed(){
    	return this._compressed;
    }
    public void setCompressed(boolean value){
    	this._compressed=value;
    }
    public byte getProtoVersion(){
    	return this._protoVersion;
    }
    public void setProtoVersion(byte ver){
    	if(ver>127)
            throw new CSONException("Э��汾�����ֵ���ܳ���127");
        this._protoVersion = ver;
    }
    public int read(byte[] carrier, int offset)
    {
        byte headByte = carrier[offset];
        //��ȡ�Ƿ�ѹ��
        this._compressed = headByte<0;
        //��ȡ�汾��Ϣ
        this._protoVersion = (byte)(headByte & 127);
        return 1;
    }
    public void write(java.io.OutputStream stream) throws IOException
    {
        byte headByte = 0;
        //д���Ƿ�ѹ����ֵ
        byte b_1 = (byte)(this._compressed ? 1 : 0);
        headByte |= (byte)(b_1 << 7);
        //д��汾��Ϣ
        headByte = (byte)(((headByte >> 7) << 7) | this._protoVersion);
        stream.write(headByte);
    }
    public void read(java.io.InputStream stream) throws IOException
    {
        byte headByte =(byte)stream.read();
        //��ȡ�Ƿ�ѹ��
        this._compressed = headByte< 0;
        //��ȡ�汾��Ϣ
        this._protoVersion = (byte)(headByte & 127);
    }
}
