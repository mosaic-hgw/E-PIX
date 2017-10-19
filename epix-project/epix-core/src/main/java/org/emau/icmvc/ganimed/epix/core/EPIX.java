package org.emau.icmvc.ganimed.epix.core;

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

import java.util.Map;

import org.apache.log4j.Logger;
import org.emau.icmvc.ganimed.deduplication.DeduplicationEngine;
import org.emau.icmvc.ganimed.epix.core.gen.MPIGenerator;
import org.emau.icmvc.ganimed.epix.core.persistence.model.IDType;
import org.emau.icmvc.ganimed.epix.core.persistence.model.PersonPreprocessed;
import org.emau.icmvc.ganimed.epix.core.report.AbstractReportBuilder;

public abstract class EPIX {

	protected EPIXContext context;

	protected AbstractReportBuilder reportBuilder = null;

	protected DeduplicationEngine<PersonPreprocessed> deduplicationEngine = null;

	protected EPIXModelMapper modelMapper = null;

	protected Map<String, MPIGenerator> generators = null;

	protected Logger logger = Logger.getLogger(EPIX.class);

	public EPIX(EPIXContext context) {
		this.context = context;
	}

	public EPIXContext getContext() {
		return context;
	}

	protected synchronized MPIGenerator getGenerator() {
		IDType type = context.getProject().getIdType();
		MPIGenerator gen = generators.get(type.getValue());
		if (gen == null) {
			logger.error("No generator found for IDType " + type.getValue() + ".");
		}
		return gen;
	}

	public DeduplicationEngine<PersonPreprocessed> getDeduplicationEngine() {
		return deduplicationEngine;
	}

	public void setDeduplicationEngine(DeduplicationEngine<PersonPreprocessed> deduplicationEngine) {
		this.deduplicationEngine = deduplicationEngine;
	}

	public EPIXModelMapper getModelMapper() {
		return modelMapper;
	}

	public void setModelMapper(EPIXModelMapper modelMapper) {
		this.modelMapper = modelMapper;
	}

	public Map<String, MPIGenerator> getGenerators() {
		return generators;
	}

	public void setGenerators(Map<String, MPIGenerator> generators) {
		this.generators = generators;
	}

	public AbstractReportBuilder getReportBuilder() {
		return reportBuilder;
	}

	public void setReportBuilder(AbstractReportBuilder reportBuilder) {
		this.reportBuilder = reportBuilder;
	}

}
