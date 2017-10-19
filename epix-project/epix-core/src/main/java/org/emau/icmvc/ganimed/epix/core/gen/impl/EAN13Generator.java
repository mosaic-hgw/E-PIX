package org.emau.icmvc.ganimed.epix.core.gen.impl;

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

import java.sql.Timestamp;

import org.emau.icmvc.ganimed.epix.common.MPIException;
import org.emau.icmvc.ganimed.epix.common.model.ErrorCode;
import org.emau.icmvc.ganimed.epix.core.EPIXContext;
import org.emau.icmvc.ganimed.epix.core.gen.MPIGeneratorStrategy;
import org.emau.icmvc.ganimed.epix.core.impl.EPIXMPIIdCounterImpl;
import org.emau.icmvc.ganimed.epix.core.persistence.dao.IdentifierDomainDAO;
import org.emau.icmvc.ganimed.epix.core.persistence.model.Identifier;
import org.emau.icmvc.ganimed.epix.core.persistence.model.IdentifierDomain;
import org.emau.icmvc.ganimed.epix.core.persistence.model.Project;
import org.emau.icmvc.ganimed.epix.core.persistence.model.enums.IdentifierTypeEnum;

/**
 * 
 * @author Christian Schack
 * 
 */
public class EAN13Generator extends MPIGeneratorStrategy {

	public EAN13Generator(EPIXContext ctx) {
		super(ctx);
	}

	@Override
	public Identifier generate() throws MPIException {
		Project project = ctx.getProject();

		EPIXMPIIdCounterImpl iEpixMpiIdCounter = EPIXMPIIdCounterImpl.getInstance();
		Long idCounter = 0l;
		try {
			idCounter = iEpixMpiIdCounter.getCounter(project.getId(), ctx);
		} catch (MPIException e) {
			String message = ErrorCode.getMessage(e.getErrorCode());
			logger.error("Can not get idCounter with given projectId: " + message);
			return null;
		}

		// Long idCounter = mpiIdDAO.getNextIndexByProjectId(project.getId());

		String mpiidValue = generate(idCounter, project.getId());
		
		IdentifierDomainDAO identifierDomainDAO = ctx.getIdentifierDomainDAO();
		IdentifierDomain mpiDomain = identifierDomainDAO.findMPIDomain(project);

		Identifier identifier = new Identifier();
		identifier.setValue(mpiidValue);
		identifier.setDomain(mpiDomain);
		identifier.setIdentifierType(IdentifierTypeEnum.MPI);
		identifier.setEntryDate(new Timestamp(System.currentTimeMillis()));

		return identifier;
	}

	public String generate(Long mpiid, Long projectNumber) {

		long codeWithoutCheckSum = 100000000000l + mpiid;
		String mpi = String.valueOf(mpiid);

		if (mpiid <= 0 || mpi.length() > 8) {
			throw new IllegalArgumentException("MPI-ID must have a max length of 8 and must be greater than 0.");
		}

		//
		String temp = String.valueOf(codeWithoutCheckSum);

		String cwcs = temp.substring(0, 3);
		cwcs += String.valueOf(projectNumber);
		cwcs += temp.substring(4, temp.length());
		logger.debug("MPI-ID without checksum: " + cwcs);

		// Filling the number into an array of int
		int[] code = new int[12];
		for (int i = 0; i < 12; i++) {
			code[i] = Integer.valueOf(cwcs.substring(i, i + 1)).intValue();
		}

		int checksum = 0;

		// Checksum calculation
		// alternate multiplication of each number with 1 (MOD 2 == 0) and 3
		// (MOD 2 != 0) and creating the sum
		// of the products.
		for (int i = 0; i < 12; i++) {
			if (i % 2 == 0) {
				checksum = checksum + code[i];
			} else {
				checksum = checksum + code[i] * 3;
			}
		}

		// Difference between current checksum and the next multiple of ten
		int diff = checksum % 10;
		diff = 10 - diff;

		return cwcs + String.valueOf(diff == 10 ? 0 : diff);
	}

	public long generate(int mpiid) {

		long codeWithoutCheckSum = 100000000000l + mpiid;
		String mpi = String.valueOf(mpiid);

		if (mpiid <= 0 || mpi.length() > 8) {
			throw new IllegalArgumentException("MPI-ID must have a max length of 8 and must be greater than 0.");
		}

		String cwcs = String.valueOf(codeWithoutCheckSum);

		// Filling the number into an array of int
		int[] code = new int[12];
		for (int i = 0; i < 12; i++) {
			code[i] = Integer.valueOf(cwcs.substring(i, i + 1)).intValue();
		}

		int checksum = 0;

		// Checksum calculation
		// alternate multiplication of each number with 1 (MOD 2 == 0) and 3
		// (MOD 2 != 0) and creating the sum
		// of the products.
		for (int i = 0; i < 12; i++) {
			if (i % 2 == 0) {
				checksum = checksum + code[i];
			} else {
				checksum = checksum + code[i] * 3;
			}
		}

		// Difference between current checksum and the next multiple of ten
		int diff = checksum % 10;
		diff = 10 - diff;
		return Long.valueOf(cwcs + String.valueOf(diff == 10 ? 0 : diff)).longValue();
	}

	@Override
	public boolean checkConsistence(String mpiidValue) throws MPIException {
		if (mpiidValue != null && !"".equals(mpiidValue)) {
			String cwcs = mpiidValue.substring(0, mpiidValue.length() - 1);
			if (cwcs.length() != 12) {
				return false;
			}
			// Filling the number into an array of int
			int[] code = new int[12];
			for (int i = 0; i < 12; i++) {
				code[i] = Integer.valueOf(cwcs.substring(i, i + 1)).intValue();
			}

			int checksum = 0;

			// Checksum calculation
			// alternate multiplication of each number with 1 (MOD 2 == 0) and 3
			// (MOD 2 != 0) and creating the sum
			// of the products.
			for (int i = 0; i < 12; i++) {
				if (i % 2 == 0) {
					checksum = checksum + code[i];
				} else {
					checksum = checksum + code[i] * 3;
				}
			}

			// Difference between current checksum and the next multiple of ten
			int diff = checksum % 10;
			diff = 10 - diff;
			String compareString = cwcs + String.valueOf(diff == 10 ? 0 : diff);
			if (compareString.equalsIgnoreCase(mpiidValue)) {
				return true;
			}
		}
		return false;
	}

}
