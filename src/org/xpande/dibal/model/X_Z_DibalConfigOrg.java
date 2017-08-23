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

/** Generated Model for Z_DibalConfigOrg
 *  @author Adempiere (generated) 
 *  @version Release 3.9.0 - $Id$ */
public class X_Z_DibalConfigOrg extends PO implements I_Z_DibalConfigOrg, I_Persistent 
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20170822L;

    /** Standard Constructor */
    public X_Z_DibalConfigOrg (Properties ctx, int Z_DibalConfigOrg_ID, String trxName)
    {
      super (ctx, Z_DibalConfigOrg_ID, trxName);
      /** if (Z_DibalConfigOrg_ID == 0)
        {
			setAD_OrgTrx_ID (0);
			setDibal_Tienda (null);
			setZ_DibalConfig_ID (0);
			setZ_DibalConfigOrg_ID (0);
        } */
    }

    /** Load Constructor */
    public X_Z_DibalConfigOrg (Properties ctx, ResultSet rs, String trxName)
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
      StringBuffer sb = new StringBuffer ("X_Z_DibalConfigOrg[")
        .append(get_ID()).append("]");
      return sb.toString();
    }

	/** Set Trx Organization.
		@param AD_OrgTrx_ID 
		Performing or initiating organization
	  */
	public void setAD_OrgTrx_ID (int AD_OrgTrx_ID)
	{
		if (AD_OrgTrx_ID < 1) 
			set_Value (COLUMNNAME_AD_OrgTrx_ID, null);
		else 
			set_Value (COLUMNNAME_AD_OrgTrx_ID, Integer.valueOf(AD_OrgTrx_ID));
	}

	/** Get Trx Organization.
		@return Performing or initiating organization
	  */
	public int getAD_OrgTrx_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_AD_OrgTrx_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Dibal_CodRapido.
		@param Dibal_CodRapido 
		Codigo para CodRapido de interface Dibal
	  */
	public void setDibal_CodRapido (String Dibal_CodRapido)
	{
		set_Value (COLUMNNAME_Dibal_CodRapido, Dibal_CodRapido);
	}

	/** Get Dibal_CodRapido.
		@return Codigo para CodRapido de interface Dibal
	  */
	public String getDibal_CodRapido () 
	{
		return (String)get_Value(COLUMNNAME_Dibal_CodRapido);
	}

	/** Set Dibal_Familia.
		@param Dibal_Familia 
		Codigo de familia para interface Dibal
	  */
	public void setDibal_Familia (String Dibal_Familia)
	{
		set_Value (COLUMNNAME_Dibal_Familia, Dibal_Familia);
	}

	/** Get Dibal_Familia.
		@return Codigo de familia para interface Dibal
	  */
	public String getDibal_Familia () 
	{
		return (String)get_Value(COLUMNNAME_Dibal_Familia);
	}

	/** Set Dibal_Seccion.
		@param Dibal_Seccion 
		Codigo de seccion para interface Dibal
	  */
	public void setDibal_Seccion (String Dibal_Seccion)
	{
		set_Value (COLUMNNAME_Dibal_Seccion, Dibal_Seccion);
	}

	/** Get Dibal_Seccion.
		@return Codigo de seccion para interface Dibal
	  */
	public String getDibal_Seccion () 
	{
		return (String)get_Value(COLUMNNAME_Dibal_Seccion);
	}

	/** Set Dibal_Subfamilia.
		@param Dibal_Subfamilia 
		Codigo de subfamilia para interface Dibal
	  */
	public void setDibal_Subfamilia (String Dibal_Subfamilia)
	{
		set_Value (COLUMNNAME_Dibal_Subfamilia, Dibal_Subfamilia);
	}

	/** Get Dibal_Subfamilia.
		@return Codigo de subfamilia para interface Dibal
	  */
	public String getDibal_Subfamilia () 
	{
		return (String)get_Value(COLUMNNAME_Dibal_Subfamilia);
	}

	/** Set Dibal_Tienda.
		@param Dibal_Tienda 
		Valor de Tienda para Dibal
	  */
	public void setDibal_Tienda (String Dibal_Tienda)
	{
		set_Value (COLUMNNAME_Dibal_Tienda, Dibal_Tienda);
	}

	/** Get Dibal_Tienda.
		@return Valor de Tienda para Dibal
	  */
	public String getDibal_Tienda () 
	{
		return (String)get_Value(COLUMNNAME_Dibal_Tienda);
	}

	public I_Z_DibalConfig getZ_DibalConfig() throws RuntimeException
    {
		return (I_Z_DibalConfig)MTable.get(getCtx(), I_Z_DibalConfig.Table_Name)
			.getPO(getZ_DibalConfig_ID(), get_TrxName());	}

	/** Set Z_DibalConfig ID.
		@param Z_DibalConfig_ID Z_DibalConfig ID	  */
	public void setZ_DibalConfig_ID (int Z_DibalConfig_ID)
	{
		if (Z_DibalConfig_ID < 1) 
			set_Value (COLUMNNAME_Z_DibalConfig_ID, null);
		else 
			set_Value (COLUMNNAME_Z_DibalConfig_ID, Integer.valueOf(Z_DibalConfig_ID));
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

	/** Set Z_DibalConfigOrg ID.
		@param Z_DibalConfigOrg_ID Z_DibalConfigOrg ID	  */
	public void setZ_DibalConfigOrg_ID (int Z_DibalConfigOrg_ID)
	{
		if (Z_DibalConfigOrg_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_Z_DibalConfigOrg_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_Z_DibalConfigOrg_ID, Integer.valueOf(Z_DibalConfigOrg_ID));
	}

	/** Get Z_DibalConfigOrg ID.
		@return Z_DibalConfigOrg ID	  */
	public int getZ_DibalConfigOrg_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_Z_DibalConfigOrg_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}
}