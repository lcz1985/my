package com.li.cson;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;

public class STRUCTS {
	public class STRUCTITEM
    {
        // �ṹ�б��е�����
        public int structIndex;
        
        // �ṹ�а����ļ�����
        public List<Integer> keyIndexs;
        
        //�ṹ������
        public String structName;
        public void write(java.io.OutputStream stream, int x) throws IOException
        {
        	int snlen=0;
        	byte[] snbyts=null; 
        	if(this.structName!=null&&this.structName.length()>0){
        		snbyts=this.structName.getBytes(CSON2.StringEncoding);
        		snlen=snbyts.length;
        		if(snlen>Byte.MAX_VALUE)
        			throw new CSONException(String.format("�޷�����CSON���л����ṹ��%s���ƹ���,�ṹ�����Ʋ�������%d���ַ�",this.structName,Byte.MAX_VALUE/2));
        	}
        	stream.write(snlen);
        	if(snlen>0){
        		stream.write(snbyts,0,snbyts.length);
        	}
        		
            for (int i = 0, j = 0; i < this.keyIndexs.size(); i++, j += x)
            {
                if (x == 1)
                    stream.write(this.keyIndexs.get(i));
                else
                {
                    byte[] keyibyts = Utils.getBinary((short)(int)this.keyIndexs.get(i));
                    stream.write(keyibyts, 0, keyibyts.length);
                }
            }
            //д���ս��
            stream.write(0);
            if (x == 2)
                stream.write(0);
        }
        public int read(byte[] carrier, int index, int x)
        {
            this.keyIndexs = new ArrayList<Integer>();
            int bytelen = 0;
            int snlen=carrier[index+bytelen++];
            if(snlen>0){
	            byte[] snbyts=new byte[snlen];
	            for(int i=0;i<snbyts.length;i++){
	            	snbyts[i]=carrier[index+bytelen++];
	            }
	            try {
					this.structName=new String(snbyts,CSON2.StringEncoding);
				} catch (UnsupportedEncodingException e) {
				}
            }
            for (int i = index+bytelen; ; i+=x)
            {
                int keyi;
                if (x == 1)
                    keyi = carrier[i];
                else
                    keyi = Utils.toInt16(carrier, i);
                bytelen += x;
                //��ȡ���ս��������
                if (keyi == 0)
                    break;
                this.keyIndexs.add(keyi);
            }
            return bytelen;
        }
        public void read(java.io.InputStream stream, int x) throws IOException
        {
            this.keyIndexs = new ArrayList<Integer>();
            int snlen=stream.read();
            if(snlen>0){
	            byte[] snbyts=new byte[snlen];
	            stream.read(snbyts);
	            try {
					this.structName=new String(snbyts,CSON2.StringEncoding);
				} catch (UnsupportedEncodingException e) {
				}
            }
            for (int i = 0; ; i += x)
            {
                int keyi;
                if (x == 1)
                    keyi = stream.read();
                else
                    keyi = Utils.toInt16(stream);
                //��ȡ���ս��������
                if (keyi == 0)
                    break;
                this.keyIndexs.add(keyi);
            }
        }
    }
	 private List<STRUCTITEM> _items;
     private Dictionary<String, STRUCTITEM> _itemsDict;
    /* private String getId(List<Integer> keyis)
     {
         StringBuilder idbl = new StringBuilder(keyis.size() * 2 - 1);
         for (int i = 0; i < keyis.size(); i++)
         {
             if (i > 0)
                 idbl.append("#");
             idbl.append(keyis.get(i));
         }
         String id = idbl.toString();
         return id;
     }*/
     public int count(){
    	 return this._items.size();
     }
     public int addStruct(String id,List<Integer> keyis)
     {
         if (keyis == null || keyis.size() == 0)
             return 0;
         if (_items == null)
         {
             _items = new ArrayList<STRUCTITEM>();
         }
         //����ṹ�������Ѿ��ﵽ���ֵ���׳��쳣
         if (_items.size() == Byte.MAX_VALUE)
             throw new CSONException(String.format("���������ѴﵽCSON��ʽ���������֧�ֵ�%d�������ͻ��߽ṹ�壡",Byte.MAX_VALUE));
         if (_itemsDict == null)
         {
             if (_items.size() > 0)
             {
                 _itemsDict = new Hashtable<String, STRUCTITEM>(_items.size());
                 for(STRUCTITEM itm:_items)
                 {
                     //_itemsDict.put(getId(itm.keyIndexs), itm);
                     _itemsDict.put(id, itm);
                 }
             }
             else
             {
                 _itemsDict = new Hashtable<String, STRUCTITEM>();
             }
         }
         //String id = this.getId(keyis);
         STRUCTITEM item=_itemsDict.get(id);
         if (item==null)
         {
             item = new STRUCTITEM();
             item.keyIndexs = keyis;
             item.structName=id;
             _items.add(item);
             item.structIndex = _items.size();
             _itemsDict.put(id, item);
         }
         return item.structIndex;
     }
    /* public List<Integer> getStructKeyIndexs(int structIndex)
     {
         return _items.get(structIndex - 1).keyIndexs;
     }*/
     public STRUCTITEM getStruct(int structIndex){
    	 return _items.get(structIndex-1);
     }
     public int read(byte[] carrier, int index,int x)
     {
         int bytelen=0;
         //��ȡ�ṹ������
         int count = carrier[index];
         bytelen+=1;
         //��ʼ������
         this._items = new ArrayList<STRUCTITEM>(count);
         this._itemsDict = new Hashtable<String, STRUCTITEM>(count);
         //ѭ����ȡ�����Ľṹ������
         for (int i = 0; i < count; i++)
         {
        	 STRUCTITEM item = new STRUCTITEM();
             bytelen+=item.read(carrier, index + bytelen, x);
             this._items.add(item);
             item.structIndex = this._items.size();
         }
         return bytelen;
     }
     public void read(java.io.InputStream stream, int x) throws IOException
     {
         //��ȡ�ṹ������
         int count = stream.read();
         //��ʼ������
         this._items = new ArrayList<STRUCTITEM>(count);
         this._itemsDict = new Hashtable<String, STRUCTITEM>(count);
         //ѭ����ȡ�����Ľṹ������
         for (int i = 0; i < count; i++)
         {
        	 STRUCTITEM item = new STRUCTITEM();
             item.read(stream, x);
             this._items.add(item);
             item.structIndex = this._items.size();
         }
     }
     public void write(java.io.OutputStream stream, int x) throws IOException
     {
         int count = this._items == null ? 0 : this._items.size();
         //д��ṹ������
         stream.write(count);
         if (count == 0)
             return;
         //ѭ��д��ṹ��
         for (STRUCTITEM itm:this._items)
         {
             itm.write(stream, x);
         }
     }
}
