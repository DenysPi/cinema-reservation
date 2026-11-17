

type TextFieldProps = {
    label: string;
    value: string;
    onChange: (value: string) => void;
    type?: string;
    required?: boolean
}

export default function TextField({label, value, onChange, type='text', ...rest}: TextFieldProps){
    return (
        <label>
            {label}
            <input type={type} value={value} onChange={(e) => e.target.value} {...rest}/>
        </label>

    )
}