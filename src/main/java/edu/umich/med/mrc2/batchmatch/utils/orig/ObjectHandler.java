///////////////////////////////////////////
// ObjectHandler.java
// Written by Jan Wigginton, December 2015
///////////////////////////////////////////
package edu.umich.med.mrc2.batchmatch.utils.orig;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;

public class ObjectHandler {
	public static String validateNonEmpty(Object object) {
		StringBuilder sb = new StringBuilder();
		Field[] fieldList = object.getClass().getDeclaredFields();

		try {
			for (int i = 0; i < fieldList.length; i++) {
				Field field = fieldList[i];
				String val = "";

				if (Modifier.toString(field.getModifiers()).contains("static"))
					continue;

				val = getStringVal(field, object);

				if (StringUtils.checkEmptyOrNull(val)) {
					String fieldLabel = StringUtils.camelToWords(field.getName());
					sb.append("Field " + fieldLabel + " cannot be empty.  Value was " + val
							+ System.getProperty("line.separator"));
				}
			}
		} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException f) {
			f.printStackTrace();
		}

		return sb.toString();
	}

	public static Map<String, String> createObjectMap(Object object) {
		Map<String, String> map = new HashMap<String, String>();
		Field[] fieldList = object.getClass().getDeclaredFields();

		for (int i = 0; i < fieldList.length; i++) {
			Field field = fieldList[i];
			String fieldLabel = StringUtils.camelToWords(field.getName());

			if (Modifier.toString(field.getModifiers()).contains("static"))
				continue;

			try {
				String val = getStringVal(field, object);
				map.put(fieldLabel, val);
			} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException f) {
				f.printStackTrace();
			}
		}

		return map;
	}

	public static String printObject(Object obj) {
		return createObjectMap(obj).toString();
	}

	private static String getStringVal(Field field, Object object)
			throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		switch (field.getType().getName()) {
		case "java.lang.String":
			return ((String) PropertyUtils.getProperty(object, field.getName()));

		case "java.lang.Character":
			Character valCh = ((Character) PropertyUtils.getProperty(object, field.getName()));
			return (valCh == null ? "" : valCh.toString());

		case "java.math.BigDecimal":
			BigDecimal valBd = ((BigDecimal) PropertyUtils.getProperty(object, field.getName()));
			return (valBd == null ? "" : valBd.toEngineeringString());

		case "java.lang.Integer":
			Integer intVal = (((Integer) PropertyUtils.getProperty(object, field.getName())));
			return (intVal == null ? "" : intVal.toString());

		case "java.lang.Double":
			Double dblVal = (((Double) PropertyUtils.getProperty(object, field.getName())));
			return (dblVal == null ? "" : dblVal.toString());

		case "java.lang.Boolean":
			Boolean boolVal = (((Boolean) PropertyUtils.getProperty(object, field.getName())));
			return (boolVal == null ? "" : boolVal.toString());

		case "java.util.Calendar":
			Calendar calVal = (((Calendar) PropertyUtils.getProperty(object, field.getName())));
			return (calVal == null ? "" : DateUtils.dateStrFromCalendar("MM/dd/yyyy", calVal));

		default:
			return "";
		}

	}
}
