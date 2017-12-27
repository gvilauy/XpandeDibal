/******************************************************************************
 * Product: ADempiere ERP & CRM Smart Business Solution                       *
 * Copyright (C) 2006-2017 ADempiere Foundation, All Rights Reserved.         *
 * This program is free software, you can redistribute it and/or modify it    *
 * under the terms version 2 of the GNU General Public License as published   *
 * or (at your option) any later version.										*
 * by the Free Software Foundation. This program is distributed in the hope   *
 * that it will be useful, but WITHOUT ANY WARRANTY, without even the implied *
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.           *
 * See the GNU General Public License for more details.                       *
 * You should have received a copy of the GNU General Public License along    *
 * with this program, if not, write to the Free Software Foundation, Inc.,    *
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.                     *
 * For the text or an alternative of this public license, you may reach us    *
 * or via info@adempiere.net or http://www.adempiere.net/license.html         *
 *****************************************************************************/
/** Generated Model - DO NOT CHANGE */
package org.xpande.dibal.model;

import java.sql.ResultSet;
import java.util.Properties;
import org.compiere.model.*;

/** Generated Model for Z_DibalConfig
 *  @author Adempiere (generated) 
 *  @version Release 3.9.0 - $Id$ */
public class X_Z_DibalConfig extends PO implements I_Z_DibalConfig, I_Persistent 
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20171227L;

    /** Standard Constructor */
    public X_Z_DibalConfig (Properties ctx, int Z_DibalConfig_ID, String trxName)
    {
      super (ctx, Z_DibalConfig_ID, trxName);
      /** if (Z_DibalConfig_ID == 0)
        {
			setSeparadorArchivoOut (null);
			setValue (null);
			setZ_DibalConfig_ID (0);
        } */
    }

    /** Load Constructor */
    public X_Z_DibalConfig (Properties ctx, ResultSet rs, String trxName)
    {
      super (ctx, rs, trxName);
    }

    /** AccessLevel
      * @return 3 - Client - Org 
      */
    protected int get_AccessLevel()
    {
      return accessLevel.intValue();
    }

    /** Load Meta Data */
    protected POInfo initPO (Properties ctx)
    {
      POInfo poi = POInfo.getPOInfo (ctx, Table_ID, get_TrxName());
      return poi;
    }

    public String toString()
    {
      StringBuffer sb = new StringBuffer ("X_Z_DibalConfig[")
        .append(get_ID()).append("]");
      return sb.toString();
    }

	/** Set ArchivoBatch.
		@param ArchivoBatch 
		Nombre del archivo Batch de interface de salida de Sisteco
	  */
	public void setArchivoBatch (String ArchivoBatch)
	{
		set_Value (COLUMNNAME_ArchivoBatch, ArchivoBatch);
	}

	/** Get ArchivoBatch.
		@return Nombre del archivo Batch de interface de salida de Sisteco
	  */
	public String getArchivoBatch () 
	{
		return (String)get_Value(COLUMNNAME_ArchivoBatch);
	}

	/** Set RutaInterfaceOut.
		@param RutaInterfaceOut 
		Ruta donde se crean los archivos de interface de salida para Sisteco
	  */
	public void setRutaInterfaceOut (String RutaInterfaceOut)
	{
		set_Value (COLUMNNAME_RutaInterfaceOut, RutaInterfaceOut);
	}

	/** Get RutaInterfaceOut.
		@return Ruta donde se crean los archivos de interface de salida para Sisteco
	  */
	public String getRutaInterfaceOut () 
	{
		return (String)get_Value(COLUMNNAME_RutaInterfaceOut);
	}

	/** Set RutaInterfaceOutHist.
		@param RutaInterfaceOutHist 
		Ruta donde dejar archivos de historico de interface de salida a Sisteco
	  */
	public void setRutaInterfaceOutHist (String RutaInterfaceOutHist)
	{
		set_Value (COLUMNNAME_RutaInterfaceOutHist, RutaInterfaceOutHist);
	}

	/** Get RutaInterfaceOutHist.
		@return Ruta donde dejar archivos de historico de interface de salida a Sisteco
	  */
	public String getRutaInterfaceOutHist () 
	{
		return (String)get_Value(COLUMNNAME_RutaInterfaceOutHist);
	}

	/** Set SeparadorArchivoOut.
		@param SeparadorArchivoOut 
		Separador de campos del archivo de interface de salida de Sisteco
	  */
	public void setSeparadorArchivoOut (String SeparadorArchivoOut)
	{
		set_Value (COLUMNNAME_SeparadorArchivoOut, SeparadorArchivoOut);
	}

	/** Get SeparadorArchivoOut.
		@return Separador de campos del archivo de interface de salida de Sisteco
	  */
	public String getSeparadorArchivoOut () 
	{
		return (String)get_Value(COLUMNNAME_SeparadorArchivoOut);
	}

	/** Set Search Key.
		@param Value 
		Search key for the record in the format required - must be unique
	  */
	public void setValue (String Value)
	{
		set_Value (COLUMNNAME_Value, Value);
	}

	/** Get Search Key.
		@return Search key for the record in the format required - must be unique
	  */
	public String getValue () 
	{
		return (String)get_Value(COLUMNNAME_Value);
	}

	/** Set Z_DibalConfig ID.
		@param Z_DibalConfig_ID Z_DibalConfig ID	  */
	public void setZ_DibalConfig_ID (int Z_DibalConfig_ID)
	{
		if (Z_DibalConfig_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_Z_DibalConfig_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_Z_DibalConfig_ID, Integer.valueOf(Z_DibalConfig_ID));
	}

	/** Get Z_DibalConfig ID.
		@return Z_DibalConfig ID	  */
	public int getZ_DibalConfig_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_Z_DibalConfig_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}
}