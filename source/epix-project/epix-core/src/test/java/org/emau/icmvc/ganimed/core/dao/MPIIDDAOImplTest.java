package org.emau.icmvc.ganimed.core.dao;

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

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.emau.icmvc.ganimed.epix.core.EPIXContext;
import org.emau.icmvc.ganimed.epix.core.impl.EPIXContextImpl;
import org.emau.icmvc.ganimed.epix.core.notifier.EPIXNotifierFactory;
import org.emau.icmvc.ganimed.epix.core.persistence.dao.IdentifierDAO;
import org.emau.icmvc.ganimed.epix.core.persistence.dao.impl.IdentifierDAOImpl;
import org.emau.icmvc.ganimed.epix.core.persistence.model.Identifier;
import org.emau.icmvc.ganimed.epix.core.persistence.model.Project;
import org.junit.Before;
import org.junit.Test;

public class MPIIDDAOImplTest {

	private EntityManager em;

	@Before
	public void setUp() throws Exception {
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("epix_new");
		em = emf.createEntityManager();
	}

	@Test
	public void TestMPIIDDAOImplTest() {
		try {

			EPIXContext context = new EPIXContextImpl(new EPIXNotifierFactory());
//			context.setEntityManager(em);

			IdentifierDAO identifierDAO = new IdentifierDAOImpl();
			identifierDAO.setEntityManager(em);

			context.setIdentifierDAO(identifierDAO);

			Project project = new Project();
			project.setId(2l);

			Identifier ident = identifierDAO.getlastMpiIdentifierByProject(project);
			long mpiId = 0l;
			if (ident != null && !ident.getValue().isEmpty()) {
				mpiId = Long.parseLong(ident.getValue().substring(4, 12));
			}
			mpiId=mpiId+1l;

			System.out.println("Last MPIID Count +1: " + mpiId);

			String newMpiId;

			newMpiId = generate(mpiId, project.getId());

			System.out.println("New generated MPIId: " + newMpiId);

		} catch (Exception e) {
			e.printStackTrace();
		}

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

}
