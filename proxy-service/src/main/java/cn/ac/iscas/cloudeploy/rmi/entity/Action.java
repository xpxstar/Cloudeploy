/**
 * Autogenerated by Thrift Compiler (0.9.1)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
package cn.ac.iscas.cloudeploy.rmi.entity;

import org.apache.thrift.scheme.IScheme;
import org.apache.thrift.scheme.SchemeFactory;
import org.apache.thrift.scheme.StandardScheme;

import org.apache.thrift.scheme.TupleScheme;
import org.apache.thrift.protocol.TTupleProtocol;
import org.apache.thrift.protocol.TProtocolException;
import org.apache.thrift.EncodingUtils;
import org.apache.thrift.TException;
import org.apache.thrift.async.AsyncMethodCallback;
import org.apache.thrift.server.AbstractNonblockingServer.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.EnumMap;
import java.util.Set;
import java.util.HashSet;
import java.util.EnumSet;
import java.util.Collections;
import java.util.BitSet;
import java.nio.ByteBuffer;
import java.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Action implements org.apache.thrift.TBase<Action, Action._Fields>, java.io.Serializable, Cloneable, Comparable<Action> {
  private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("Action");

  private static final org.apache.thrift.protocol.TField TEMPLATE_FIELD_DESC = new org.apache.thrift.protocol.TField("template", org.apache.thrift.protocol.TType.STRING, (short)1);
  private static final org.apache.thrift.protocol.TField TARGET_MODULE_FIELD_DESC = new org.apache.thrift.protocol.TField("targetModule", org.apache.thrift.protocol.TType.STRUCT, (short)2);

  private static final Map<Class<? extends IScheme>, SchemeFactory> schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>();
  static {
    schemes.put(StandardScheme.class, new ActionStandardSchemeFactory());
    schemes.put(TupleScheme.class, new ActionTupleSchemeFactory());
  }

  public String template; // required
  public Module targetModule; // required

  /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
  public enum _Fields implements org.apache.thrift.TFieldIdEnum {
    TEMPLATE((short)1, "template"),
    TARGET_MODULE((short)2, "targetModule");

    private static final Map<String, _Fields> byName = new HashMap<String, _Fields>();

    static {
      for (_Fields field : EnumSet.allOf(_Fields.class)) {
        byName.put(field.getFieldName(), field);
      }
    }

    /**
     * Find the _Fields constant that matches fieldId, or null if its not found.
     */
    public static _Fields findByThriftId(int fieldId) {
      switch(fieldId) {
        case 1: // TEMPLATE
          return TEMPLATE;
        case 2: // TARGET_MODULE
          return TARGET_MODULE;
        default:
          return null;
      }
    }

    /**
     * Find the _Fields constant that matches fieldId, throwing an exception
     * if it is not found.
     */
    public static _Fields findByThriftIdOrThrow(int fieldId) {
      _Fields fields = findByThriftId(fieldId);
      if (fields == null) throw new IllegalArgumentException("Field " + fieldId + " doesn't exist!");
      return fields;
    }

    /**
     * Find the _Fields constant that matches name, or null if its not found.
     */
    public static _Fields findByName(String name) {
      return byName.get(name);
    }

    private final short _thriftId;
    private final String _fieldName;

    _Fields(short thriftId, String fieldName) {
      _thriftId = thriftId;
      _fieldName = fieldName;
    }

    public short getThriftFieldId() {
      return _thriftId;
    }

    public String getFieldName() {
      return _fieldName;
    }
  }

  // isset id assignments
  public static final Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
  static {
    Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(_Fields.class);
    tmpMap.put(_Fields.TEMPLATE, new org.apache.thrift.meta_data.FieldMetaData("template", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    tmpMap.put(_Fields.TARGET_MODULE, new org.apache.thrift.meta_data.FieldMetaData("targetModule", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.StructMetaData(org.apache.thrift.protocol.TType.STRUCT, Module.class)));
    metaDataMap = Collections.unmodifiableMap(tmpMap);
    org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(Action.class, metaDataMap);
  }

  public Action() {
  }

  public Action(
    String template,
    Module targetModule)
  {
    this();
    this.template = template;
    this.targetModule = targetModule;
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public Action(Action other) {
    if (other.isSetTemplate()) {
      this.template = other.template;
    }
    if (other.isSetTargetModule()) {
      this.targetModule = new Module(other.targetModule);
    }
  }

  public Action deepCopy() {
    return new Action(this);
  }

  @Override
  public void clear() {
    this.template = null;
    this.targetModule = null;
  }

  public String getTemplate() {
    return this.template;
  }

  public Action setTemplate(String template) {
    this.template = template;
    return this;
  }

  public void unsetTemplate() {
    this.template = null;
  }

  /** Returns true if field template is set (has been assigned a value) and false otherwise */
  public boolean isSetTemplate() {
    return this.template != null;
  }

  public void setTemplateIsSet(boolean value) {
    if (!value) {
      this.template = null;
    }
  }

  public Module getTargetModule() {
    return this.targetModule;
  }

  public Action setTargetModule(Module targetModule) {
    this.targetModule = targetModule;
    return this;
  }

  public void unsetTargetModule() {
    this.targetModule = null;
  }

  /** Returns true if field targetModule is set (has been assigned a value) and false otherwise */
  public boolean isSetTargetModule() {
    return this.targetModule != null;
  }

  public void setTargetModuleIsSet(boolean value) {
    if (!value) {
      this.targetModule = null;
    }
  }

  public void setFieldValue(_Fields field, Object value) {
    switch (field) {
    case TEMPLATE:
      if (value == null) {
        unsetTemplate();
      } else {
        setTemplate((String)value);
      }
      break;

    case TARGET_MODULE:
      if (value == null) {
        unsetTargetModule();
      } else {
        setTargetModule((Module)value);
      }
      break;

    }
  }

  public Object getFieldValue(_Fields field) {
    switch (field) {
    case TEMPLATE:
      return getTemplate();

    case TARGET_MODULE:
      return getTargetModule();

    }
    throw new IllegalStateException();
  }

  /** Returns true if field corresponding to fieldID is set (has been assigned a value) and false otherwise */
  public boolean isSet(_Fields field) {
    if (field == null) {
      throw new IllegalArgumentException();
    }

    switch (field) {
    case TEMPLATE:
      return isSetTemplate();
    case TARGET_MODULE:
      return isSetTargetModule();
    }
    throw new IllegalStateException();
  }

  @Override
  public boolean equals(Object that) {
    if (that == null)
      return false;
    if (that instanceof Action)
      return this.equals((Action)that);
    return false;
  }

  public boolean equals(Action that) {
    if (that == null)
      return false;

    boolean this_present_template = true && this.isSetTemplate();
    boolean that_present_template = true && that.isSetTemplate();
    if (this_present_template || that_present_template) {
      if (!(this_present_template && that_present_template))
        return false;
      if (!this.template.equals(that.template))
        return false;
    }

    boolean this_present_targetModule = true && this.isSetTargetModule();
    boolean that_present_targetModule = true && that.isSetTargetModule();
    if (this_present_targetModule || that_present_targetModule) {
      if (!(this_present_targetModule && that_present_targetModule))
        return false;
      if (!this.targetModule.equals(that.targetModule))
        return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    return 0;
  }

  @Override
  public int compareTo(Action other) {
    if (!getClass().equals(other.getClass())) {
      return getClass().getName().compareTo(other.getClass().getName());
    }

    int lastComparison = 0;

    lastComparison = Boolean.valueOf(isSetTemplate()).compareTo(other.isSetTemplate());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetTemplate()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.template, other.template);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetTargetModule()).compareTo(other.isSetTargetModule());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetTargetModule()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.targetModule, other.targetModule);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    return 0;
  }

  public _Fields fieldForId(int fieldId) {
    return _Fields.findByThriftId(fieldId);
  }

  public void read(org.apache.thrift.protocol.TProtocol iprot) throws org.apache.thrift.TException {
    schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
  }

  public void write(org.apache.thrift.protocol.TProtocol oprot) throws org.apache.thrift.TException {
    schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder("Action(");
    boolean first = true;

    sb.append("template:");
    if (this.template == null) {
      sb.append("null");
    } else {
      sb.append(this.template);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("targetModule:");
    if (this.targetModule == null) {
      sb.append("null");
    } else {
      sb.append(this.targetModule);
    }
    first = false;
    sb.append(")");
    return sb.toString();
  }

  public void validate() throws org.apache.thrift.TException {
    // check for required fields
    // check for sub-struct validity
    if (targetModule != null) {
      targetModule.validate();
    }
  }

  private void writeObject(java.io.ObjectOutputStream out) throws java.io.IOException {
    try {
      write(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(out)));
    } catch (org.apache.thrift.TException te) {
      throw new java.io.IOException(te);
    }
  }

  private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException {
    try {
      read(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(in)));
    } catch (org.apache.thrift.TException te) {
      throw new java.io.IOException(te);
    }
  }

  private static class ActionStandardSchemeFactory implements SchemeFactory {
    public ActionStandardScheme getScheme() {
      return new ActionStandardScheme();
    }
  }

  private static class ActionStandardScheme extends StandardScheme<Action> {

    public void read(org.apache.thrift.protocol.TProtocol iprot, Action struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TField schemeField;
      iprot.readStructBegin();
      while (true)
      {
        schemeField = iprot.readFieldBegin();
        if (schemeField.type == org.apache.thrift.protocol.TType.STOP) { 
          break;
        }
        switch (schemeField.id) {
          case 1: // TEMPLATE
            if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
              struct.template = iprot.readString();
              struct.setTemplateIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 2: // TARGET_MODULE
            if (schemeField.type == org.apache.thrift.protocol.TType.STRUCT) {
              struct.targetModule = new Module();
              struct.targetModule.read(iprot);
              struct.setTargetModuleIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          default:
            org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
        }
        iprot.readFieldEnd();
      }
      iprot.readStructEnd();

      // check for required fields of primitive type, which can't be checked in the validate method
      struct.validate();
    }

    public void write(org.apache.thrift.protocol.TProtocol oprot, Action struct) throws org.apache.thrift.TException {
      struct.validate();

      oprot.writeStructBegin(STRUCT_DESC);
      if (struct.template != null) {
        oprot.writeFieldBegin(TEMPLATE_FIELD_DESC);
        oprot.writeString(struct.template);
        oprot.writeFieldEnd();
      }
      if (struct.targetModule != null) {
        oprot.writeFieldBegin(TARGET_MODULE_FIELD_DESC);
        struct.targetModule.write(oprot);
        oprot.writeFieldEnd();
      }
      oprot.writeFieldStop();
      oprot.writeStructEnd();
    }

  }

  private static class ActionTupleSchemeFactory implements SchemeFactory {
    public ActionTupleScheme getScheme() {
      return new ActionTupleScheme();
    }
  }

  private static class ActionTupleScheme extends TupleScheme<Action> {

    @Override
    public void write(org.apache.thrift.protocol.TProtocol prot, Action struct) throws org.apache.thrift.TException {
      TTupleProtocol oprot = (TTupleProtocol) prot;
      BitSet optionals = new BitSet();
      if (struct.isSetTemplate()) {
        optionals.set(0);
      }
      if (struct.isSetTargetModule()) {
        optionals.set(1);
      }
      oprot.writeBitSet(optionals, 2);
      if (struct.isSetTemplate()) {
        oprot.writeString(struct.template);
      }
      if (struct.isSetTargetModule()) {
        struct.targetModule.write(oprot);
      }
    }

    @Override
    public void read(org.apache.thrift.protocol.TProtocol prot, Action struct) throws org.apache.thrift.TException {
      TTupleProtocol iprot = (TTupleProtocol) prot;
      BitSet incoming = iprot.readBitSet(2);
      if (incoming.get(0)) {
        struct.template = iprot.readString();
        struct.setTemplateIsSet(true);
      }
      if (incoming.get(1)) {
        struct.targetModule = new Module();
        struct.targetModule.read(iprot);
        struct.setTargetModuleIsSet(true);
      }
    }
  }

}

