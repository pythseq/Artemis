/********************************************************************
*
*  This library is free software; you can redistribute it and/or
*  modify it under the terms of the GNU Library General Public
*  License as published by the Free Software Foundation; either
*  version 2 of the License, or (at your option) any later version.
*
*  This library is distributed in the hope that it will be useful,
*  but WITHOUT ANY WARRANTY; without even the implied warranty of
*  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
*  Library General Public License for more details.
*
*  You should have received a copy of the GNU Library General Public
*  License along with this library; if not, write to the
*  Free Software Foundation, Inc., 59 Temple Place - Suite 330,
*  Boston, MA  02111-1307, USA.
*
*  Copyright (C) Genome Research Limited
*
********************************************************************/

package uk.ac.sanger.artemis.components.filetree;

import java.awt.datatransfer.*;
import javax.swing.tree.*;
import java.io.*;
import java.util.*;

/**
*
* File node for local file tree manager
*
*/
public class FileNode extends DefaultMutableTreeNode 
                 implements Transferable, Serializable
{
    private boolean isDir;
    /** data flavour of a file node */
    public static DataFlavor FILENODE =
           new DataFlavor(FileNode.class, "Local file");
    /** flavours file node and string */
    static DataFlavor flavors[] = { FILENODE, DataFlavor.stringFlavor };

    /**
    *
    * @param file	file node file
    *
    */
    public FileNode(File file)
    { 
      setUserObject(file); 
      this.isDir = file.isDirectory();
    }

    /** Determine if this is a directory */
    public boolean getAllowsChildren() { return isDirectory(); }
    /** Determine if this is a file */
    public boolean isLeaf() { return !isDirectory(); }
    /** Get the File this node represents */
    public File getFile() { return (File)getUserObject(); }
    /** Determine if this is a directory */
    public boolean isDirectory() 
    { 
      return isDir;
    }

    public void setDirectory(boolean isDir)
    {
      this.isDir = isDir;
    }

    /**
    *
    * Returns the name of the file 
    *
    */
    public String toString() 
    {
      File file = (File)getUserObject();
      String filename = file.toString();
      int index = filename.lastIndexOf(File.separator);

      return (index != -1 && index != filename.length()-1) ? 
                          filename.substring(index+1) : 
                                            filename;
    }

    private Object child_cache[];

    public Object[] getChildren(FileFilter filter)
    {
      if(!isDirectory())
        return null;

      if(child_cache != null)
        return child_cache;

      File file = getFile();
      File[] children = file.listFiles(filter);
 
      if(children == null)
        return null;
      
//    if(child_cache != null &&
//       child_cache.length == children.length)
//      return child_cache;

// sort into alphabetic order
      java.util.Arrays.sort(children);
      child_cache = new Object[children.length];
      for(int i=0; i < children.length; ++i)
      {
        child_cache[i] = new FileNode(children[i]);
        ((FileNode)child_cache[i]).setParent(this);
      }
      return child_cache;
    }

    public void reset()
    {
      child_cache = null;
    }


// Transferable
    public DataFlavor[] getTransferDataFlavors()
    {
      return flavors;
    }

    public boolean isDataFlavorSupported(DataFlavor f)
    {
      if(f.equals(FILENODE) || f.equals(DataFlavor.stringFlavor))
        return true;
      return false;
    }

    public Object getTransferData(DataFlavor d)
        throws UnsupportedFlavorException, IOException
    {
      if(d.equals(FILENODE))
        return this;
      else if(d.equals(DataFlavor.stringFlavor))
        return getFile().getAbsolutePath();
      else throw new UnsupportedFlavorException(d);
    }

//Serializable
   private void writeObject(java.io.ObjectOutputStream out) throws IOException
   {
     out.defaultWriteObject();
   }

   private void readObject(java.io.ObjectInputStream in)
     throws IOException, ClassNotFoundException
   {
     in.defaultReadObject();
   }

}
