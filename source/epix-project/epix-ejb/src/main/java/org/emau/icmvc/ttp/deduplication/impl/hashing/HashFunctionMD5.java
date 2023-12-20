package org.emau.icmvc.ttp.deduplication.impl.hashing;

import org.apache.commons.codec.digest.DigestUtils;
import org.emau.icmvc.ttp.epix.common.deduplication.IHashFunction;

/**
 * @author Christopher Hampf
 */
public class HashFunctionMD5 implements IHashFunction
{
	/**
	 * Returns the md5 hash of given string.
	 *
	 * @param value string that is hashed
	 *
	 * @return Given string as md5 hash
	 */
	@Override
	public byte[] hash(String value)
	{
		return DigestUtils.md5(value.getBytes());
	}
}
