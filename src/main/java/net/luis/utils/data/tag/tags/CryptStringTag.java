package net.luis.utils.data.tag.tags;

import java.io.DataInput;
import java.io.DataOutput;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.SecretKeySpec;

import net.luis.utils.data.tag.Tag;
import net.luis.utils.data.tag.TagType;
import net.luis.utils.data.tag.exception.LoadTagException;
import net.luis.utils.data.tag.exception.SaveTagException;
import net.luis.utils.data.tag.visitor.TagVisitor;
import net.luis.utils.util.Equals;

/**
 *
 * @author Luis-st
 *
 */

public class CryptStringTag implements Tag {
	
	public static final TagType<CryptStringTag> TYPE = new TagType<CryptStringTag>() {
		@Override
		public CryptStringTag load(DataInput input) throws LoadTagException {
			try {
				Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
				byte[] byteKey = new byte[128];
				input.readFully(byteKey);
				cipher.init(Cipher.DECRYPT_MODE, SecretKeyFactory.getInstance("DES").generateSecret(new SecretKeySpec(byteKey, "DES")));
				return valueOf(new String(cipher.doFinal(input.readUTF().getBytes())));
			} catch (Exception e) {
				throw new LoadTagException(e);
			}
		}
		
		@Override
		public String getName() {
			return "crypt_string_tag";
		}
		
		@Override
		public String getVisitorName() {
			return "CryptStringTag";
		}
	};
	public static final CryptStringTag EMPTY = new CryptStringTag("");
	
	private final String data;
	
	private CryptStringTag(String data) {
		this.data = data;
	}
	
	public static CryptStringTag valueOf(String data) {
		if (data == null) {
			return EMPTY;
		}
		return data.isEmpty() ? EMPTY : new CryptStringTag(data);
	}
	
	@Override
	public void save(DataOutput output) throws SaveTagException {
		try {
			Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
			byte[] byteKey = createKey();
			cipher.init(Cipher.ENCRYPT_MODE, SecretKeyFactory.getInstance("DES").generateSecret(new SecretKeySpec(byteKey, "DES")));
			output.write(byteKey);
			output.writeUTF(new String(cipher.doFinal(this.data.getBytes())));
		} catch (Exception e) {
			throw new SaveTagException(e);
		}
	}
	
	private byte[] createKey() {
		SecureRandom rng = new SecureRandom();
		rng.ints(System.currentTimeMillis());
		byte[] key = new byte[128];
		for (int i = 0; i < key.length; i++) {
			int j = rng.nextInt(key.length * (i + 1)) + (key.length - i);
			key[i] = (byte) (j % key.length);
		}
		return key;
	}
	
	@Override
	public byte getId() {
		return STRING_TAG;
	}
	
	@Override
	public TagType<CryptStringTag> getType() {
		return TYPE;
	}
	
	@Override
	public CryptStringTag copy() {
		return valueOf(this.data);
	}
	
	@Override
	public void accept(TagVisitor visitor) {
		visitor.visitCryptString(this);
	}
	
	@Override
	public String getAsString() {
		return this.data;
	}
	
	@Override
	public String toString() {
		return Tag.super.getAsString();
	}
	
	@Override
	public boolean equals(Object object) {
		return Equals.equals(this, object);
	}
	
	@Override
	public int hashCode() {
		return this.data.hashCode();
	}
	
}
