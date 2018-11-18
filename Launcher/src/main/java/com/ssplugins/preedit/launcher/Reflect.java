package com.ssplugins.preedit.launcher;

import java.lang.reflect.*;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Stream;

public class Reflect<T> {

    private TypeReference<T> ref;
    
    private Reflect(TypeReference<T> ref) {
        this.ref = ref;
    }
    
    public static <I> Reflect<I> of(I i) {
        TypeReference<I> ref = new TypeReference<>(i);
        ref.set(i);
        return new Reflect<>(ref);
    }
    
    public static <I> Reflect<I> of(Class<I> type) {
        TypeReference<I> ref = new TypeReference<>(type);
        return new Reflect<>(ref);
    }
    
    public TypeReference<T> getReference() {
        return ref;
    }
    
    public <O> Optional<O> as(Class<O> type) {
        return getReference().as(type);
    }
    
    public <O> O getAs(Class<O> type) {
        return getReference().getAs(type);
    }
    
    public T get() {
        return getReference().getValue();
    }
    
    public Optional<T> asOptional() {
        return getReference().asOptional();
    }
    
    public <O> Reflect<O> cast(Class<O> type) {
        if (ref.getValue() == null) {
            return new Reflect<>(ref.setType(type));
        }
        else {
            type = (Class<O>) ReflectionTools.wrap(type);
            if (type.isAssignableFrom(ReflectionTools.wrap(ref.getValue().getClass()))) {
                TypeReference<O> r = new TypeReference<>(type);
                r.setValueObject(ref.getValue());
                return new Reflect<>(r);
            }
            else throw new IllegalArgumentException();
        }
    }
    
    public boolean canCast(Class<?> type) {
        if (ref.getValue() == null) return true;
        else {
            type = ReflectionTools.wrap(type);
            return type.isAssignableFrom(ReflectionTools.wrap(ref.getValue().getClass()));
        }
    }
    
    public Reflect<?> method(String methodName, Object... args) {
        Optional<Method> method = findMethod(methodName, args);
        if (!method.isPresent()) {
            ref.set(null);
            return this;
        }
        return new Reflect<>(ReflectionTools.callMethod(ref, method.get(), args));
    }
    
    public Optional<Method> findMethod(String methodName, Object... args) {
        return ReflectionTools.findMethod(ref, methodName, args);
    }
    
    public Reflect<T> construct(Object... args) {
        Class<T> type = ref.getType();
        if (type == null) throw new IllegalStateException("No type has been set to construct.");
        Optional<Constructor<T>> con = findConstructor(type, args);
        if (!con.isPresent()) {
            ref.set(null);
            return this;
        }
        return new Reflect<>(ReflectionTools.callConstructor(ref, con.get(), args));
    }
    
    public static <O> Optional<Constructor<O>> findConstructor(Class<O> type, Object... args) {
        return ReflectionTools.findConstructor(type, args);
    }
    
    public Reflect<?> field(String fieldName) {
        Optional<Field> field = findField(fieldName);
        if (!field.isPresent()) {
            ref.set(null);
            return this;
        }
        return new Reflect<>(ReflectionTools.getField(ref, field.get()));
    }
    
    public Optional<Field> findField(String fieldName) {
        return ReflectionTools.findField(ref, fieldName);
    }
    
    public static class TypeReference<R> {
        
        private R value;
        private Class<R> type;
        
        private TypeReference(R r, Class<R> type) {}
    
        private TypeReference(Class<R> type) {
            this.type = type;
        }
    
        private TypeReference(R r) {
            value = r;
        }
    
        public TypeReference<R> copy() {
            return new TypeReference<>(value, type);
        }
        
        public R getValue() {
            return value;
        }
        
        void setValue(R value) {
            this.value = value;
        }
    
        void setValueObject(Object o) {
            value = type.cast(o);
        }
        
        public Class<R> getType() {
            if (type == null) {
                if (value == null) return null;
                return (Class<R>) value.getClass();
            }
            return type;
        }
        
        <O> TypeReference<O> setType(Class<O> type) {
            TypeReference<O> ref = new TypeReference<>(type);
            if (value != null && type.isAssignableFrom(value.getClass())) ref.setValue(type.cast(value));
            return ref;
        }
        
        void set(R o) {
            setValue(o);
            if (o == null) type = null;
            else type = (Class<R>) o.getClass();
        }
        
        public Optional<R> asOptional() {
            return Optional.ofNullable(value);
        }
    
        public <O> Optional<O> as(Class<O> type) {
            return asOptional().filter(o -> type.isAssignableFrom(o.getClass())).map(type::cast);
        }
        
        public <O> O getAs(Class<O> type) {
            return as(type).orElseThrow(IllegalArgumentException::new);
        }
        
    }
    
    private static final class ReflectionTools {
        
        static Optional<Method> findMethod(TypeReference<?> ref, String methodName, Object[] params) {
            Optional<Method> method = Optional.empty();
            Class<?> toCheck;
            if (ref.getValue() != null) toCheck = ref.getValue().getClass();
            else toCheck = ref.getType();
            while (toCheck != null) {
                method = methodSearch(toCheck, methodName, params);
                if (method.isPresent()) break;
                for (Class<?> i : toCheck.getInterfaces()) {
                    method = methodSearch(i, methodName, params);
                    if (method.isPresent()) break;
                }
                if (method.isPresent()) break;
                toCheck = toCheck.getSuperclass();
            }
            return method;
        }
        
        static TypeReference<?> callMethod(TypeReference<?> ref, Method method, Object[] params) {
            if (method.isVarArgs()) {
                Class<?>[] types = method.getParameterTypes();
                params = toVarArgs(params, method.getParameterCount() - 1, types[types.length - 1].getComponentType());
            }
            method.setAccessible(true);
            TypeReference<?> out;
            try {
                out = new TypeReference<>(wrap(method.getReturnType()));
                out.setValueObject(method.invoke(ref.getValue(), params));
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new IllegalStateException("Unable to call method " + methodToString(method), e);
            }
            return out;
        }
        
        static <O> Optional<Constructor<O>> findConstructor(Class<O> type, Object[] params) {
            Optional<Constructor<?>> con = Stream.of(type.getDeclaredConstructors())
                                                   .filter(constructor -> {
                                                       return searchSignature(params, constructor.getParameterTypes(), constructor.isVarArgs());
                                                   })
                                                   .findFirst();
            return con.map(constructor -> {
                try {
                    return type.getDeclaredConstructor(constructor.getParameterTypes());
                } catch (NoSuchMethodException e) {
                    return null;
                }
            });
        }
        
        private static boolean searchSignature(Object[] params, Class<?>[] parameterTypes, boolean varArgs) {
            if (varArgs) {
                if (params.length < parameterTypes.length - 1) return false;
                if (notAssignable(params, parameterTypes)) return false;
                Class<?> arrayType = parameterTypes[parameterTypes.length - 1].getComponentType();
                for (int i = parameterTypes.length - 1; i < params.length; i++) {
                    Class<?> p = params[i].getClass();
                    if (!arrayType.isAssignableFrom(unwrap(p)) && !arrayType.isAssignableFrom(p)) return false;
                }
                return true;
            }
            if (parameterTypes.length != params.length) return false;
            return !notAssignable(params, parameterTypes);
        }
        
        private static boolean notAssignable(Object[] params, Class<?>[] parameterTypes) {
            for (int i = 0; i < parameterTypes.length; i++) {
                if (params[i] == null) continue;
                Class<?> p = params[i].getClass();
                Class<?> pt = parameterTypes[i];
                if (!pt.isAssignableFrom(unwrap(p)) && !pt.isAssignableFrom(p)) return true;
            }
            return false;
        }
        
        static <O> TypeReference<O> callConstructor(TypeReference<O> ref, Constructor<O> con, Object[] params) {
            if (con.isVarArgs()) {
                Class<?>[] types = con.getParameterTypes();
                params = toVarArgs(params, con.getParameterCount() - 1, types[types.length - 1].getComponentType());
            }
            con.setAccessible(true);
            try {
                ref.set(con.newInstance(params));
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                throw new IllegalStateException("Unable to call constructor " + constructorToString(con));
            }
            return ref;
        }
        
        static Optional<Field> findField(TypeReference<?> ref, String fieldName) {
            try {
                return Optional.of(ref.getType().getDeclaredField(fieldName));
            } catch (NoSuchFieldException e) {
                return Optional.empty();
            }
        }
        
        static TypeReference<?> getField(TypeReference<?> ref, Field field) {
            field.setAccessible(true);
            TypeReference<?> out;
            try {
                out = new TypeReference<>(wrap(field.getType()));
                out.setValueObject(field.get(ref.getValue()));
            } catch (IllegalAccessException e) {
                throw new IllegalStateException("Unable to get field " + fieldToString(field));
            }
            return out;
        }
        
        private static Object[] toVarArgs(Object[] src, int index, Class<?> baseType) {
            int len = src.length - index;
            Object arr = Array.newInstance(baseType, len);
            for (int i = 0; i < len; i++) {
                Array.set(arr, i, src[i + index]);
            }
            src[index] = arr;
            return Arrays.copyOf(src, index + 1);
        }
        
        private static Optional<Method> methodSearch(Class<?> type, String methodName, Object[] params) {
            return Stream.of(type.getDeclaredMethods())
                         .filter(method -> method.getName().equals(methodName))
                         .filter(method -> {
                             return searchSignature(params, method.getParameterTypes(), method.isVarArgs());
                         })
                         .findFirst();
        }
        
        private static Class<?> unwrap(Class<?> type) {
            if (type.equals(Integer.class)) return int.class;
            else if (type.equals(Double.class)) return double.class;
            else if (type.equals(Boolean.class)) return boolean.class;
            else if (type.equals(Long.class)) return long.class;
            else if (type.equals(Character.class)) return char.class;
            else if (type.equals(Byte.class)) return byte.class;
            else if (type.equals(Short.class)) return short.class;
            else if (type.equals(Float.class)) return float.class;
            return type;
        }
    
        private static Class<?> wrap(Class<?> type) {
            if (type.equals(int.class)) return Integer.class;
            else if (type.equals(double.class)) return Double.class;
            else if (type.equals(boolean.class)) return Boolean.class;
            else if (type.equals(long.class)) return Long.class;
            else if (type.equals(char.class)) return Character.class;
            else if (type.equals(byte.class)) return Byte.class;
            else if (type.equals(short.class)) return Short.class;
            else if (type.equals(float.class)) return Float.class;
            return type;
        }
        
        private static String methodToString(Method method) {
            StringBuilder builder = new StringBuilder();
            builder.append(method.getDeclaringClass().getSimpleName())
                   .append("#")
                   .append(method.getName())
                   .append("(");
            Class<?>[] types = method.getParameterTypes();
            addClassNames(builder, types);
            if (method.isVarArgs()) builder.append("...");
            builder.append(")");
            return builder.toString();
        }
        
        private static void addClassNames(StringBuilder builder, Class<?>[] types) {
            for (int i = 0; i < types.length; i++) {
                Class<?> c = types[i];
                builder.append(c.getSimpleName());
                if (i < types.length - 1) builder.append(", ");
            }
        }
        
        private static String constructorToString(Constructor<?> con) {
            StringBuilder builder = new StringBuilder();
            builder.append(con.getDeclaringClass().getSimpleName())
                   .append("(");
            Class<?>[] types = con.getParameterTypes();
            addClassNames(builder, types);
            if (con.isVarArgs()) builder.append("...");
            builder.append(")");
            return builder.toString();
        }
        
        private static String fieldToString(Field field) {
            return field.getDeclaringClass().getSimpleName() + "#" + field.getName();
        }
        
    }
    
}