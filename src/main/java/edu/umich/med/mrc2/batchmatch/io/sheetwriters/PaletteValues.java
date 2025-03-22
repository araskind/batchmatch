////////////////////////////////////////////////////
// PaletteValues.java
// Written by Jan Wigginton, Feb 1, 2017
////////////////////////////////////////////////////
package edu.umich.med.mrc2.batchmatch.io.sheetwriters;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

//"FF3D49", "FF3E48", "FF4D47", "FF4247", "FF4446", "FF4645", "FF4845", "FF4A44", "FF4C43","FF4C43",
public class PaletteValues implements Serializable {
	private static final long serialVersionUID = -4828536112263573140L;

	public static final List<String> anotherPalette = Arrays.asList(new String[] { "fff5c9", "ffe0e6", "c9e5ce",
			"add2d8", "e3c4a0", "bbbbcb", "98aeca", "ffcb94", "feea92", "ffc1cc", "92cb9c", "5aa4b0", "c78841",
			"767697", "315d94", "ffd107", "ff96aa", "92cb9c", "5aa4b0", "cc6d02", "767697", "8dadd6" });

	public static final List<String> massDiffPaletteValues = Arrays
			.asList(new String[] { "FF0000", "C37FFF", "92FFB1", "C6FF46", "756600", "B2D576", "B95B92", "FFCC00",
					"FFAA7F", "929FFF", "007570", "5D7DBC", "D5CA76", "00FF4C", "F0FF7F", "FF9296", "C246FF", "3864c1",
					"BC6B5D", "71C7CF", "0019FF", "7FFFF7", "FF9346", "6b24a5", "750038" });

	public static final List<String> histogramPaletteValues = Arrays
			.asList(new String[] { "FFF5C9", "F9CAD3", "C9E5CE", "ADD2D8", "FFCB94", "BBBBCB", "98AECA" });

	public static final List<String> paletteValues1 = Arrays.asList(new String[] { "FF4E43", "FF5042", "FF5241",
			"FF5441", "FF5640", "FF583F", "FF5A3F", "FF5C3E", "FF5E3D", "FF603D", "FF623C", "FF643B", "FF683A",
			"FF6A39", "FF6C39", "FF6E38", "FF7037", "FF7237", "FF7436", "FF7635", "FF7835", "FF7A34", "FF7B33",
			"FF7D33", "FF7F32", "FF8131", "FF8131", "FF8831", "FF8530", "FF872F", "FF892F", "FF8B2E", "FF8F2D",
			"FF912C", "FF932B", "FF952B", "FF972A", "FF9929", "FF9B29", "FF9D28", "FF9F27", "FFA127", "FFA326",
			"FFA525", "FFA725", "FFA924", "FFAB23", "FFAD23", "FFAF22", "FFB121", "FFB321", "FFB520", "FFB71F",
			"FFB91F", "FFBF24", "FFC62A", "FFCD2F", "FFD435", "FFDB3B", "FFE240", "FFE945", "FFF04B", "FFF751",
			"FFFE57", "F6F858", "EEF359", "E6ED5A", "DEE85B", "D6E25C", "CEDD5C", "C5D75E", "BDD25F", "B5CC60",
			"ADC162", "9DBC63", "8CB165", "84AB66", "7CA667", "74A068", "6C9B69" });

	public static final List<String> paletteValues2 = Arrays
			.asList(new String[] { "0035E5", "0043E5", "0051E5", "005FE6", "006DE6", "007BE6", "008AE7", "0098E7",
					"00A6E8", "01B5E8", "01C3E8", "01D1E9", "01E0E9", "01E9E5", "01EAD7", "01EAC9",

					"01EBBB", "02EBAD", "02EBA0", "02EC92", "02EC84", "02ED76", "02ED68", "02ED5A",

					"02EE4C", "02EE3D", "03EE2F", "03EF21", "03EF13", "03F005", "10F003", "1EF003", "2DF103", "3BF104",
					"4AF104", "59F204", "67F204", "76F304", "85F304", "94F304", "A2F404", "B1F405", "C0F505", "CFF505",
					"DEF505", "EDF605", "F6F005", "F6E205", "F7D305", "F7C506", "F8B706", "F8A806", "F89A06", "F98C06",
					"F97D06", "F96F06", "FA6007", "FA5107", "FB4307", "FB3407", "FC1707", "FC0807", "FD0816" });

	public static final List<String> paletteValues3 = Arrays.asList(new String[] { "0035E5", "0434E1", "0833DE",
			"0C32DB", "1032D7", "1431D4", "1830D1", "1C30CE", "202FCA", "242EC7", "282DC4", "2C2DC0", "302CBD",
			"342BBA", "382BB7", "3C2AB3", "4029B0", "4428AD", "4828A9", "4C27A6", "5026A3", "5426A0", "58259C",
			"5C2499", "602396", "642392", "68228F", "6C218C", "702189", "742085", "781F82", "7C1E7F",

			"801E7B", "841D78", "881C75", "8C1C72", "901B6E", "941A6B", "981968", "9C1964", "A01861", "A4175E",
			"A8175B", "AC1657", "B01554", "B41451", "B8144D", "BC134A", "C01247", "C41244", "C81140", "CC103D",
			"D00F3A", "D40F36", "D80E33", "DC0D30", "E00D2D", "E40C29", "E80B26", "EC0A23", "F00A1F", "F4091C",
			"F80819", "FD0816" });

	public static final List<String> paletteValues4 = Arrays.asList(new String[] { "FB0817", "F90819", "F7091A",
			"F5091C", "F3091E", "F10A1F", "EF0A21", "ED0A22", "EB0B24", "E90B26", "E70B27", "E50C29", "E30C2A",
			"E10C2C", "DF0D2E", "DD0D2F", "DB0D31", "D90E32", "D70E34", "D50E36", "D30F37", "D10F39", "CF103A",
			"CD103C", "CB103E", "C9113F", "C71141", "C51142", "C31244", "C11246", "BF1247", "BD1349", "BB134A",
			"B9134C", "B7144E", "B5144F", "B31451", "B11552", "AF1554", "AD1556", "AB1657", "A91659", "A7175A",
			"A5175C", "A3175E", "A1185F", "9F1861", "9D1862", "9B1964", "991966", "971967", "951A69", "931A6A",
			"911A6C", "8F1B6E", "8D1B6F", "8B1B71", "891C72", "871C74", "851C76", "831D77", "811D79", "801E7B",
			"7C1E7F", "7B1D82", "7A1D85", "791D88", "771C8C", "751C8F", "731B92", "711B96", "6E1A99", "6B1A9C",
			"67199F", "6318A3", "5F18A6", "5B17A9", "5616AD", "5115B0", "4C14B3", "4613B6", "4012BA", "3911BD",
			"3210C0", "2B0EC4", "230DC7", "1B0CCA", "120ACD", "0909D1", "080FD4", "0616D7", "051DDB", "0325DE", "012CE1"

	});

	public static final List<String> paletteValues5 = Arrays
			.asList(new String[] { "FB0817", "F90819", "F7091A", "F5091C", "F3091E", "F10A1F", "EF0A21", "ED0A22",
					"EB0B24", "E90B26", "E70B27", "E50C29", "E30C2A", "E10C2C", "DF0D2E", "DD0D2F", "DB0D31", "D90E32",
					"D70E34", "D50E36", "D30F37", "D10F39", "CF103A", "CD103C", "CB103E", "C9113F", "C71141", "C51142",
					"C31244", "C11246", "BF1247", "BD1349", "BB134A", "B9134C", "B7144E", "B5144F", "B31451", "B11552",
					"AF1554", "AD1556", "AB1657", "A91659", "A7175A", "A5175C", "A3175E", "A1185F", "9F1861", "9D1862",
					"9B1964", "991966", "971967", "951A69", "931A6A", "911A6C", "8F1B6E", "8D1B6F", "8B1B71", "891C72",
					"871C74", "851C76", "831D77", "811D79", "801E7B", "7C1E7F", "7B1D82", "7A1D85", "791D88", "771C8C",
					"751C8F", "731B92", "711B96", "6E1A99", "6B1A9C", "67199F", "6318A3", "5F18A6", "5B17A9", "5616AD",
					"5115B0", "4C14B3", "4613B6", "4012BA", "3911BD", "3210C0", "2B0EC4", "230DC7" });

	public static final List<String> paletteValues6 = Arrays.asList(new String[] {

			"000000", "030303", "060606", "090909", "0D0D0D", "101010", "131313", "161617", "1A1A1A", "1D1D1D",
			"202020", "232324", "272727", "2A2A2A", "2D2D2E", "313131", "343434", "373737", "3A3A3B", "3E3E3E",
			"414141", "444445", "474748", "4B4B4B", "4E4E4E", "515152", "555555", "585858", "5B5B5C", "5E5E5F",
			"626262", "656565", "686869", "6B6B6C", "6F6F6F", "727273", "757576", "787879", "7C7C7C", "7F7F80",
			"828283", "868686", "89898A", "8C8C8D", "8F8F90", "939393", "969697", "99999A", "9C9C9D", "A0A0A1",
			"A3A3A4", "A6A6A7", "AAAAAA", "ADADAE", "B0B0B1", "B3B3B4", "B7B7B8", "BABABB", "BDBDBE", "C0C0C1",
			"C4C4C5", "C7C7C8", "CACACB", "CECECF" });

	public static List<String> getReversedValues(List<String> values) {
		List<String> reversedValues = new ArrayList<String>();

		for (int i = values.size() - 1; i >= 0; i--)
			reversedValues.add(values.get(i));

		return reversedValues;
	}
}