package org.emau.icmvc.ttp.epix.common.model.config;

/*-
 * ###license-information-start###
 * E-PIX - Enterprise Patient Identifier
 * 							Cross-referencing
 * __
 * Copyright (C) 2009 - 2022 Trusted Third Party of the University Medicine Greifswald
 * 							kontakt-ths@uni-greifswald.de
 * 
 * 							concept and implementation
 * 							l.geidel,c.schack, d.langner, g.koetzschke
 * 
 * 							web client
 * 							a.blumentritt, f.m. moser
 * 
 * 							docker
 * 							r.schuldt
 * 
 * 							privacy preserving record linkage (PPRL)
 * 							c.hampf
 * 
 * 							please cite our publications
 * 							http://dx.doi.org/10.3414/ME14-01-0133
 * 							http://dx.doi.org/10.1186/s12967-015-0545-6
 * 							https://translational-medicine.biomedcentral.com/articles/10.1186/s12967-020-02257-4
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

import org.emau.icmvc.ttp.epix.common.model.enums.FieldName;

import java.io.Serializable;
import java.util.Objects;

public class SourceFieldDTO implements Serializable {
    private static final long serialVersionUID = -8123131531862812458L;
    private final FieldName name;
    private final String saltValue;
    private final FieldName saltField;
    private final long seed;

    public SourceFieldDTO(FieldName name, String saltValue, FieldName saltField, long seed) {
        this.name = name;
        this.saltValue = saltValue;
        this.saltField = saltField;
        this.seed = seed;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SourceFieldDTO that = (SourceFieldDTO) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(saltValue, that.saltValue) &&
                Objects.equals(saltField, that.saltField) &&
                Objects.equals(seed, that.seed);
    }

    public FieldName getName() {
        return name;
    }

    public String getSaltValue() {
        return saltValue;
    }

    public FieldName getSaltField() {
        return saltField;
    }

    public long getSeed() {
        return seed;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, saltValue, saltField, seed);
    }

    @Override
    public String toString() {
        return "SourceFieldDTO [name=" + name + ", saltValue=" + saltValue + ", saltField=" + saltField + ", seed=" + seed + "]";
    }
}
