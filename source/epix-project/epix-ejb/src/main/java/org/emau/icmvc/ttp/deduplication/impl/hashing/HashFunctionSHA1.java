package org.emau.icmvc.ttp.deduplication.impl.hashing;

import org.apache.commons.codec.digest.DigestUtils;
import org.emau.icmvc.ttp.epix.common.deduplication.IHashFunction;

/**
 * @author Christopher Hampf
 */
public class HashFunctionSHA1 implements IHashFunction
{
	/**
	 * Returns the sha-1 hash of given string.
	 *
	 * @param value string that is hashed
	 *
	 * @return Given string as sha-1 hash
	 */
	@Override
	public byte[] hash(String value)
	{
		return DigestUtils.sha1(value.getBytes());
	}
}
