use epix;

INSERT INTO identifier_domain VALUES ('MOSAIC','MOSAIC',current_timestamp(3),current_timestamp(3),'demo domain','1.2.276.0.76.3.1.132.100.1.1.1.3.0.1.1');
INSERT INTO domain VALUES ('MOSAIC','MOSAIC','',current_timestamp(3),current_timestamp(3),'MPI','dummy_safe_source','<?xml version="1.0" encoding="UTF-8"?>
<!--
  ###license-information-start###
  E-PIX - Enterprise Identifier Cross-Referencing
  __
  Copyright (C) 2009 - 2020 The MOSAIC Project - Institut fuer Community
  							Medicine der
  							Universitaetsmedizin Greifswald -
  							mosaic-projekt@uni-greifswald.de
  							concept and implementation
  							c.
  							schack, l. geidel, d. langner, g. koetzschke
  							web client
  							a.
  							blumentritt
  							please cite our publications
  							http://dx.doi.org/10.3414/ME14-01-0133
  							http://dx.doi.org/10.1186/s12967-015-0545-6
  __
  This program is free software: you can redistribute it and/or modify
  it under the terms of the GNU Affero General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
  (at your option) any later version.
  
  This program is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.
  
  You should have received a copy of the GNU Affero General Public License
  along with this program.  If not, see <http://www.gnu.org/licenses/>.
  ###license-information-end###
  -->

<ma:MatchingConfiguration xmlns:ma="http://www.ttp.icmvc.emau.org/deduplication/config/model" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="http://www.ttp.icmvc.emau.org/deduplication/config/model matching-config-2.7.0.xsd ">
	<matching-mode>MATCHING_IDENTITIES</matching-mode>
	<mpi-generator>org.emau.icmvc.ttp.epix.gen.impl.EAN13Generator</mpi-generator>
	<mpi-prefix>1001</mpi-prefix>
	<use-notifications>false</use-notifications>

	<required-fields>
		<name>firstName</name>
		<name>lastName</name>
		<name>birthDate</name>
		<name>gender</name>
	</required-fields>
	<preprocessing-config>
		<preprocessing-field required="true">
			<field-name>firstName</field-name>
			<simple-transformation-type xsi:type="ma:SimpleTransformation">
				<input-pattern></input-pattern>
				<output-pattern></output-pattern>
			</simple-transformation-type>
			<simple-transformation-type xsi:type="ma:SimpleTransformation">
				<input-pattern>?</input-pattern>
				<output-pattern></output-pattern>
			</simple-transformation-type>
			<simple-transformation-type xsi:type="ma:SimpleTransformation">
				<input-pattern>é</input-pattern>
				<output-pattern>e</output-pattern>
			</simple-transformation-type>
			<simple-transformation-type xsi:type="ma:SimpleTransformation">
				<input-pattern>Dr.</input-pattern>
				<output-pattern></output-pattern>
			</simple-transformation-type>
			<simple-transformation-type xsi:type="ma:SimpleTransformation">
				<input-pattern>Prof.</input-pattern>
				<output-pattern></output-pattern>
			</simple-transformation-type>
			<simple-transformation-type xsi:type="ma:SimpleTransformation">
				<input-pattern> med.</input-pattern>
				<output-pattern></output-pattern>
			</simple-transformation-type>
			<simple-transformation-type xsi:type="ma:SimpleTransformation">
				<input-pattern>rer.</input-pattern>
				<output-pattern></output-pattern>
			</simple-transformation-type>
			<simple-transformation-type xsi:type="ma:SimpleTransformation">
				<input-pattern>nat.</input-pattern>
				<output-pattern></output-pattern>
			</simple-transformation-type>
			<simple-transformation-type xsi:type="ma:SimpleTransformation">
				<input-pattern>Ing.</input-pattern>
				<output-pattern></output-pattern>
			</simple-transformation-type>
			<simple-transformation-type xsi:type="ma:SimpleTransformation">
				<input-pattern>Dipl.</input-pattern>
				<output-pattern></output-pattern>
			</simple-transformation-type>
			<simple-transformation-type xsi:type="ma:SimpleTransformation">
				<input-pattern>,</input-pattern>
				<output-pattern></output-pattern>
			</simple-transformation-type>
			<simple-transformation-type xsi:type="ma:SimpleTransformation">
				<input-pattern> </input-pattern>
				<output-pattern></output-pattern>
			</simple-transformation-type>
			<simple-transformation-type xsi:type="ma:SimpleTransformation">
				<input-pattern>-</input-pattern>
				<output-pattern></output-pattern>
			</simple-transformation-type>
			<complex-transformation-type xsi:type="ma:ComplexTransformation">
				<qualified-class-name>org.emau.icmvc.ttp.deduplication.preprocessing.impl.ToUpperCaseTransformation</qualified-class-name>
			</complex-transformation-type>
			<complex-transformation-type xsi:type="ma:ComplexTransformation">
				<qualified-class-name>org.emau.icmvc.ttp.deduplication.preprocessing.impl.CharsMutationTransformation</qualified-class-name>
			</complex-transformation-type>
		</preprocessing-field>
		<preprocessing-field>
			<field-name>lastName</field-name>
			<simple-transformation-type xsi:type="ma:SimpleTransformation">
				<input-pattern></input-pattern>
				<output-pattern></output-pattern>
			</simple-transformation-type>
			<simple-transformation-type xsi:type="ma:SimpleTransformation">
				<input-pattern>?</input-pattern>
				<output-pattern></output-pattern>
			</simple-transformation-type>
			<simple-transformation-type xsi:type="ma:SimpleTransformation">
				<input-pattern>é</input-pattern>
				<output-pattern>e</output-pattern>
			</simple-transformation-type>
			<simple-transformation-type xsi:type="ma:SimpleTransformation">
				<input-pattern>Dr.</input-pattern>
				<output-pattern></output-pattern>
			</simple-transformation-type>
			<simple-transformation-type xsi:type="ma:SimpleTransformation">
				<input-pattern>Prof.</input-pattern>
				<output-pattern></output-pattern>
			</simple-transformation-type>
			<simple-transformation-type xsi:type="ma:SimpleTransformation">
				<input-pattern>med.</input-pattern>
				<output-pattern></output-pattern>
			</simple-transformation-type>
			<simple-transformation-type xsi:type="ma:SimpleTransformation">
				<input-pattern>rer.</input-pattern>
				<output-pattern></output-pattern>
			</simple-transformation-type>
			<simple-transformation-type xsi:type="ma:SimpleTransformation">
				<input-pattern>nat.</input-pattern>
				<output-pattern></output-pattern>
			</simple-transformation-type>
			<simple-transformation-type xsi:type="ma:SimpleTransformation">
				<input-pattern>Ing.</input-pattern>
				<output-pattern></output-pattern>
			</simple-transformation-type>
			<simple-transformation-type xsi:type="ma:SimpleTransformation">
				<input-pattern>Dipl.</input-pattern>
				<output-pattern></output-pattern>
			</simple-transformation-type>
			<simple-transformation-type xsi:type="ma:SimpleTransformation">
				<input-pattern>,</input-pattern>
				<output-pattern></output-pattern>
			</simple-transformation-type>
			<simple-transformation-type xsi:type="ma:SimpleTransformation">
				<input-pattern> </input-pattern>
				<output-pattern></output-pattern>
			</simple-transformation-type>
			<simple-transformation-type xsi:type="ma:SimpleTransformation">
				<input-pattern>-</input-pattern>
				<output-pattern></output-pattern>
			</simple-transformation-type>
			<complex-transformation-type xsi:type="ma:ComplexTransformation">
				<qualified-class-name>org.emau.icmvc.ttp.deduplication.preprocessing.impl.ToUpperCaseTransformation</qualified-class-name>
			</complex-transformation-type>
			<complex-transformation-type xsi:type="ma:ComplexTransformation">
				<qualified-class-name>org.emau.icmvc.ttp.deduplication.preprocessing.impl.CharsMutationTransformation</qualified-class-name>
			</complex-transformation-type>
		</preprocessing-field>
	</preprocessing-config>
	<matching>
		<threshold-possible-match>2.99</threshold-possible-match>
		<threshold-automatic-match>14.5</threshold-automatic-match>
		<use-cemfim>false</use-cemfim>
		<field>
			<name>firstName</name>
			<threshold>0.8</threshold>
			<weight>8</weight>
			<algorithm>org.emau.icmvc.ttp.deduplication.impl.LevenstheinMatchingAlgorithm</algorithm>
		</field>
		<field>
			<name>lastName</name>
			<threshold>0.8</threshold>
			<weight>6</weight>
			<algorithm>org.emau.icmvc.ttp.deduplication.impl.LevenstheinMatchingAlgorithm</algorithm>
		</field>
		<field>
			<name>gender</name>
			<threshold>0.75</threshold>
			<weight>3</weight>
			<algorithm>org.emau.icmvc.ttp.deduplication.impl.LevenstheinMatchingAlgorithm</algorithm>
		</field>
		<field>
			<name>birthDate</name>
			<threshold>1</threshold>
			<weight>9</weight>
			<algorithm>org.emau.icmvc.ttp.deduplication.impl.LevenstheinMatchingAlgorithm</algorithm>
		</field>
	</matching>
	<blocking>
		<field>
			<name>birthDate</name>
			<threshold>0.6</threshold>
			<algorithm>org.emau.icmvc.ttp.deduplication.impl.LevenshteinBlockingAlgorithm</algorithm>
		</field>
		<field>
			<name>firstName</name>
			<threshold>0.4</threshold>
			<algorithm>org.emau.icmvc.ttp.deduplication.impl.LevenshteinBlockingAlgorithm</algorithm>
		</field>
	</blocking>
</ma:MatchingConfiguration>
');
