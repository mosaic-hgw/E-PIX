package org.emau.icmvc.ganimed.deduplication.impl;

/*
 * ###license-information-start###
 * E-PIX - Enterprise Patient Identifier Cross-referencing
 * __
 * Copyright (C) 2009 - 2017 The MOSAIC Project - Institut fuer Community Medicine der
 * 							Universitaetsmedizin Greifswald - mosaic-projekt@uni-greifswald.de
 * 							concept and implementation
 * 							c. schack, d.langner, l. geidel
 * 							web client
 * 							a. blumentritt
 * 							g. weiher
 * 							please cite our publications
 * 							http://dx.doi.org/10.3414/ME14-01-0133
 * 							http://dx.doi.org/10.1186/s12967-015-0545-6
 * __
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * ###license-information-end###
 */

import java.util.List;
import java.util.Set;

import org.emau.icmvc.ganimed.deduplication.PerfectMatchStrategy;
import org.emau.icmvc.ganimed.deduplication.model.PerfectMatchTransferObject;
import org.emau.icmvc.ganimed.epix.common.utils.ReflectionUtil;
import org.emau.icmvc.ganimed.exception.DeduplicationException;

public class CommonPerfectMatchStrategy<T> extends PerfectMatchStrategy<T> {

	@Override
	public T perfectMatch(T object) throws DeduplicationException {
		if(transferObject != null) {
			T match = getPerfectMatch(object);
			return match;
		}
		return null;
	}
	
	@Override
	public T perfectMatch(T toMatch, List<T> candidateList) throws DeduplicationException {
		T perfectMatch = null;
		if(transferObject != null) {
			for(T candidate : candidateList) {
				T pm = getPerfectMatch(toMatch, candidate);
				if(pm != null && perfectMatch != null) {
					throw new DeduplicationException("Found more then one perfectMatch for requestedPerson.");
				} else if (pm != null){
					perfectMatch = pm;
				}
			}
		}
		return perfectMatch;
	}

	private T getPerfectMatch(T object) throws DeduplicationException {
		if(object.getClass().getSuperclass().getName().equals(transferObject.getName()) ||
				object.getClass().getName().equals(transferObject.getName())) {
			Set<String> set = transferObject.getValueMap().keySet();
			for(String key : set) {
				try {
					Object obj = ReflectionUtil.getProperty(object, key);
					if(obj==null) {
						return null;
					} else if(transferObject.getValueMap().get(key) instanceof List) {
						getListType(key, obj, transferObject);
					} else {
						transferObject.getValueMap().put(key, obj);
					}
				} catch (Exception e) {
					throw new DeduplicationException(e.getLocalizedMessage(), e);
				}
			}
		}
		T result = perfectMatchProzessor.getPerfectMatch(transferObject);
		transferObject.clearValues();
		return result;
	}
	
	private T getPerfectMatch(T toMatch, T candidate) throws DeduplicationException {
		
		Set<String> set = transferObject.getValueMap().keySet();
		for(String key : set) {
			Object matchObj = null;
			Object candidateObj = null;
			try {
				matchObj = ReflectionUtil.getProperty(toMatch, key);
				candidateObj = ReflectionUtil.getProperty(candidate, key);
			} catch (Exception e) {
				throw new DeduplicationException(e.getLocalizedMessage(), e);
			}
			if(matchObj == null || candidateObj == null) {
				return null;
			} else if(transferObject.getValueMap().get(key) instanceof List) {
				if (!matchListPerfect(key, matchObj, candidateObj)) {
					return null;
				}
			} else if(!matchPerfect(matchObj, candidateObj)) {
					return null;
			}
		}
		return candidate;
	}

	private void getListType(String key, Object object, PerfectMatchTransferObject transferObject) throws DeduplicationException {
		@SuppressWarnings("unchecked")
		List<PerfectMatchTransferObject> listTransferObject = (List<PerfectMatchTransferObject>) transferObject.getValueMap().get(key);
		
		if(object instanceof List) {
			@SuppressWarnings("unchecked")
			List<Object> objectList = (List<Object>) object;
			int size = objectList.size();
			while(size > listTransferObject.size()) {
				PerfectMatchTransferObject transferObjectCopy = new PerfectMatchTransferObject();
				transferObjectCopy.setName(listTransferObject.get(0).getName());
				Set<String> keySet = listTransferObject.get(0).getValueMap().keySet();
				for(String iKey : keySet) {
					transferObjectCopy.getValueMap().put(iKey, null);
				}
				listTransferObject.add(transferObjectCopy);
			}
			
			for (int i = 0; i < objectList.size(); i++) {
				if(objectList.get(i).getClass().getName().equals(listTransferObject.get(i).getName())) {
					Set<String> keySet = listTransferObject.get(i).getValueMap().keySet();
					for(String keyS : keySet) {
						try {
							Object obj = ReflectionUtil.getProperty(objectList.get(i), keyS);
							if(obj == null) {
								return;
							} else if(listTransferObject.get(i).getValueMap().get(keyS) instanceof PerfectMatchTransferObject) {
								getListType(keyS, objectList.get(i), listTransferObject.get(i));
							} else {
								listTransferObject.get(i).getValueMap().put(keyS, obj);
							}
						} catch (Exception e) {
							throw new DeduplicationException(e.getLocalizedMessage(), e);
						}
					}
				}
			}
		}
	}
	
	private boolean matchPerfect (Object toMatch, Object candidate) {
		boolean bool = (toMatch == null && candidate == null) || (toMatch != null && toMatch.equals(candidate));
		return bool;
	}

	private boolean matchListPerfect(String key, Object matchObj, Object candidateObj) throws DeduplicationException {
		@SuppressWarnings("unchecked")
		List<PerfectMatchTransferObject> listTransferObject = (List<PerfectMatchTransferObject>) transferObject.getValueMap().get(key);
		if(matchObj instanceof List && candidateObj instanceof List) {
			@SuppressWarnings("unchecked")
			List<Object> matchObjList = (List<Object>) matchObj;
			@SuppressWarnings("unchecked")
			List<Object> candidateObjList = (List<Object>) candidateObj;
			for (Object matchObject : matchObjList) {
				for (Object candidateObject : candidateObjList) {
					if (listTransferObject.get(0).getName().equals(matchObject.getClass().getName()) &&
							listTransferObject.get(0).getName().equals(candidateObject.getClass().getName())) {
						boolean same = true;
						for (String keySet: listTransferObject.get(0).getValueMap().keySet()) {
							Object match = null;
							Object candidate = null;
							try {
								match = ReflectionUtil.getProperty(matchObject, keySet);
								candidate = ReflectionUtil.getProperty(candidateObject, keySet);
							} catch (Exception e) {
								throw new DeduplicationException(e.getLocalizedMessage(), e);
							}
							if(match == null || candidate == null) {
								same = false;
							} else if(listTransferObject.get(0).getValueMap().get(keySet) instanceof List) {
								same = matchListPerfect(key, match, candidate);
							} else {
								if(!matchPerfect(match, candidate)) {
									same = false;
								}
							}
						}
						if (same) {
							return same;
						}
					}
				}
			}
		}
		return false;
			
	}
}
