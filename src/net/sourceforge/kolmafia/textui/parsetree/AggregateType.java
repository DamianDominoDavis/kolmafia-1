package net.sourceforge.kolmafia.textui.parsetree;

import net.sourceforge.kolmafia.textui.DataTypes;

public class AggregateType extends CompositeType {
  protected final Type dataType;
  protected final Type indexType;
  protected final boolean caseInsensitive;
  protected int size;

  private AggregateType(
      final String name,
      final Type dataType,
      final Type indexType,
      final int size,
      final boolean caseInsensitive) {
    super(name, DataTypes.TYPE_AGGREGATE);
    this.dataType = dataType;
    this.indexType = indexType;
    this.size = size;
    this.caseInsensitive = caseInsensitive && indexType.equals(DataTypes.STRING_TYPE);
  }

  public AggregateType(final AggregateType original) {
    this(
        "aggregate",
        original.dataType,
        original.indexType,
        original.size,
        original.caseInsensitive);
  }

  // Map
  public AggregateType(final Type dataType, final Type indexType) {
    this("aggregate", dataType, indexType, -1, false);
  }

  // Map with case-insensitive string keys
  public AggregateType(final Type dataType, final Type indexType, boolean caseInsensitive) {
    this("aggregate", dataType, indexType, -1, caseInsensitive);
  }

  // Array
  public AggregateType(final Type dataType, final int size) {
    this("aggregate", dataType, DataTypes.INT_TYPE, size, false);
  }

  // VarArg
  public AggregateType(final String name, final Type dataType, final int size) {
    this(name, dataType, DataTypes.INT_TYPE, size, false);
  }

  @Override
  public Type getDataType() {
    return this.dataType;
  }

  @Override
  public Type getDataType(final Object key) {
    return this.dataType;
  }

  @Override
  public Type getIndexType() {
    return this.indexType;
  }

  @Override
  public Value getKey(final Value key) {
    return key;
  }

  public int getSize() {
    return this.size;
  }

  public void setSize(final int size) {
    this.size = size;
  }

  @Override
  public boolean equals(final Type o) {
    return o instanceof AggregateType
        && this.dataType.equals(((AggregateType) o).dataType)
        && this.indexType.equals(((AggregateType) o).indexType);
  }

  @Override
  public Type simpleType() {
    if (this.dataType instanceof AggregateType) {
      return this.dataType.simpleType();
    }
    return this.dataType;
  }

  @Override
  public String toString() {
    return this.simpleType().toString() + " [" + this.indexString() + "]";
  }

  public String indexString() {
    if (this.dataType instanceof AggregateType) {
      String suffix = ", " + ((AggregateType) this.dataType).indexString();
      if (this.size != -1) {
        return this.size + suffix;
      }
      return this.indexType.toString() + suffix;
    }

    if (this.size != -1) {
      return String.valueOf(this.size);
    }
    return this.indexType.toString();
  }

  @Override
  public Value initialValue() {
    return (this.size != -1) ? new ArrayValue(this) : new MapValue(this, this.caseInsensitive);
  }

  @Override
  public int dataValues() {
    if (this.size <= 0) {
      return -1;
    }
    int values = this.dataType.dataValues();
    if (values <= 0) {
      return -1;
    }
    return this.size * values;
  }
}
