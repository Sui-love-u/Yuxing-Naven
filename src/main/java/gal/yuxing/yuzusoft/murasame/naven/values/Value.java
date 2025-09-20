package gal.yuxing.yuzusoft.murasame.naven.values;

import lombok.Getter;
import gal.yuxing.yuzusoft.murasame.naven.Naven;
import gal.yuxing.yuzusoft.murasame.naven.exceptions.BadValueTypeException;
import gal.yuxing.yuzusoft.murasame.naven.values.impl.BooleanValue;
import gal.yuxing.yuzusoft.murasame.naven.values.impl.FloatValue;
import gal.yuxing.yuzusoft.murasame.naven.values.impl.ModeValue;
import gal.yuxing.yuzusoft.murasame.naven.values.impl.StringValue;

import java.util.function.Supplier;

@Getter
public abstract class Value {
    private final HasValue key;
    private final String name;
    private final Supplier<Boolean> visibility;

    protected Value(HasValue key, String name, Supplier<Boolean> visibility) {
        this.key = key;
        this.name = name;
        this.visibility = visibility;

        Naven.getInstance().getValueManager().addValue(this);
    }

    public abstract ValueType getValueType();

    public BooleanValue getBooleanValue() {
        throw new BadValueTypeException();
    }

    public FloatValue getFloatValue() {
        throw new BadValueTypeException();
    }

    public StringValue getStringValue() {
        throw new BadValueTypeException();
    }

    public ModeValue getModeValue() {
        throw new BadValueTypeException();
    }

    public boolean isVisible() {
        return visibility == null || visibility.get();
    }
}
