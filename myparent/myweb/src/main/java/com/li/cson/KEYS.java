package com.li.cson;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public class KEYS {
	public class KEYITEM
    {
        /// <summary>
        /// ����
        /// </summary>
        public int index;
        /// <summary>
        /// ������
        /// </summary>
        public byte meta;
        /// <summary>
        /// ������
        /// </summary>
        public String name;
        public void write(java.io.OutputStream stream) throws IOException
        {
            //д��meta����
            stream.write(this.meta);
            byte[] namebyts =this.name.getBytes(CSON2.StringEncoding);
            if (namebyts.length > 255)
                throw new CSONException(String.format("CSON��֧�ֳ��ȳ���%d�ļ���%s", 255, this.name));
            //д�����Ƴ���
            stream.write((byte)namebyts.length); 
            //д������
            stream.write(namebyts, 0, namebyts.length);
        }
        public void read(java.io.InputStream stream) throws IOException
        {
            //��ȡmeta����
            this.meta = (byte)stream.read();
            //��ȡ���Ƴ���
            int namebytl = stream.read();
            byte[] binary=new byte[namebytl];
            stream.read(binary, 0, namebytl);
            //��ȡ����
            this.name =new String(binary,CSON2.StringEncoding);
        }
        /// <summary>
        /// ���ֽ����ж�ȡ
        /// </summary>
        /// <param name="carrier"></param>
        /// <param name="offset"></param>
        /// <returns></returns>
        public int read(byte[] carrier, int offset)
        {
            int bytelen = 0;
            //��ȡmeta����
            this.meta = carrier[offset];
            bytelen++;
            //��ȡ���Ƴ���
            int namebytl = carrier[offset + bytelen];
            bytelen++;
            byte[] binary=new byte[namebytl];
            for(int i=0;i<binary.length;i++){
            	binary[i]=carrier[offset+bytelen++];
            }
            //��ȡ����
            try {
				this.name = new String(binary,CSON2.StringEncoding);
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				throw new RuntimeException(e);
			}
            //bytelen += namebytl;
            return bytelen;
        }
        @Override
        public String toString()
        {
            return String.format("%d %s@%d", this.index, this.name, this.meta);
        }
    }
	private Hashtable<String, KEYITEM> _keyiDict;
	private List<KEYITEM> _keyiList;
	public int getCount(){
		if (this._keyiList == null)
            return 0;
        return _keyiList.size();
	}
	public int getKeyBit(){
		if (this._keyiList == null || _keyiList.size() < Byte.MAX_VALUE)
            return 1;
        return 2;
	}
	public int addKey(byte meta, String name)
    {
        if (this._keyiList == null)
        {
            this._keyiList = new ArrayList<KEYITEM>();
        }
        if (this._keyiList.size() > 65535)
            throw new CSONException("CSON���ֻ��֧���ܹ�65535������");
        if (this._keyiDict == null)
        {
            if (this._keyiList.size()> 0)
            {
                this._keyiDict = new Hashtable<String, KEYITEM>();
                for(int i=0;i<this._keyiList.size();i++){
                	KEYITEM itm =this._keyiList.get(i);
                	this._keyiDict.put(itm.name + "@" + meta, itm);
                }
            }
            else
            {
                this._keyiDict = new Hashtable<String, KEYITEM>();
            }
        }
        String id=name+"@"+meta;
        KEYITEM item=this._keyiDict.get(id);
        if (item==null)
        {
            item = new KEYITEM();
            item.meta = meta;
            item.name = name;
            _keyiDict.put(id, item);
            _keyiList.add(item);
            item.index = _keyiList.size();
        }
        return item.index;
    }
	public KEYITEM getKey(int keyIndex)
    {
        return _keyiList.get(keyIndex-1);
    }
	public KEYITEM getKey(byte meta, String name)
    {
        String id = name + "@" + meta;
        return this._keyiDict.get(id);
    }
	public void write(java.io.OutputStream stream) throws IOException
    {
        //д�������
        short c=(short)this.getCount();
        byte[] sbinary=Utils.getBinary(c);
        stream.write(sbinary);
        if (c == 0)
            return;
        //ѭ��д���
        for(int i=0;i<c;i++){
        	KEYITEM itm=this._keyiList.get(i);
        	itm.write(stream);
        }
    }
	 public int read(byte[] carrier, int offset)
     {
         int bytelen = 0;
         //��ȡ������
         short count=Utils.toInt16(carrier, offset);
         this._keyiList = new ArrayList<KEYITEM>(count);
         bytelen += 2;
         //ѭ����ȡ����Ϣ
         for (int i = 0; i < count; i++)
         {
             KEYITEM itm = new KEYITEM();
             bytelen+=itm.read(carrier, offset + bytelen);
             this._keyiList.add(itm);
             itm.index = this._keyiList.size();
         }
         return bytelen;
     }
	 public void read(java.io.InputStream stream) throws IOException
     {
		 byte[] cb=new byte[2];
		 cb[0]=(byte)stream.read();
		 cb[1]=(byte)stream.read();
         short count =Utils.toInt16(cb,0);
         this._keyiList = new ArrayList<KEYITEM>(count);
         //ѭ����ȡ����Ϣ
         for (int i = 0; i < count; i++)
         {
             KEYITEM itm = new KEYITEM();
             itm.read(stream);
             this._keyiList.add(itm);
             itm.index = this._keyiList.size();
         }
     }
}
