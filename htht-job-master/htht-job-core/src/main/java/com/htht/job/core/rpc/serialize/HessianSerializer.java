package com.htht.job.core.rpc.serialize;


import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.KryoException;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.serializers.JavaSerializer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * hessian serialize
 *
 * @author xuxueli 2015-9-26 02:53:29
 */
public class HessianSerializer {

    public static <T> byte[] serialize(T obj) {
        try {
        /*ByteArrayOutputStream os = new ByteArrayOutputStream();
		HessianOutput ho = new HessianOutput(os);
		try {
			ho.writeObject(obj);
		} catch (IOException e) {
			throw new IllegalStateException(e.getMessage(), e);
		}
		return os.toByteArray();*/
            Kryo kryo = new Kryo();
            kryo.setReferences(false);
            kryo.register(obj.getClass(), new JavaSerializer());

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Output output = new Output(baos);
            kryo.writeClassAndObject(output, obj);
            output.flush();
            output.close();
            byte[] b = baos.toByteArray();

            return b;
        } catch (KryoException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static <T> Object deserialize(byte[] bytes, Class<T> clazz) {
        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(bytes);

		/*HessianInput hi = new HessianInput(is);
		try {
			return hi.readObject();
		} catch (IOException e) {
			throw new IllegalStateException(e.getMessage(), e);
		}*/
            Kryo kryo = new Kryo();
            kryo.setReferences(false);
            kryo.register(clazz, new JavaSerializer());
            Input input = new Input(bais);
            return kryo.readClassAndObject(input);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
