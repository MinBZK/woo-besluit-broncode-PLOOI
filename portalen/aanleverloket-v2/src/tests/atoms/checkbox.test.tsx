import { findByTestId, fireEvent, getByText, render, screen } from '@testing-library/react';
import { CheckboxAtom } from '../../ui/atoms';
import '@testing-library/jest-dom/extend-expect';

const props = {
    id: "checkbox",
    checked: false,
    disabled: false,
    bold: false,
    onClick: () => { },
    label: "Checkbox Label",
};

it('Renders the CheckboxAtom',  () => {
    const { getByText } = render(<CheckboxAtom {...props}>
        children
    </CheckboxAtom>);

    const cb = getByText(/checkbox label/i);
    expect(cb).toBeInTheDocument();
});

it('is disabled', () => {
    const { getByText } = render(<CheckboxAtom {...props} disabled={true} />);
    const cb = getByText(/checkbox label/i);

    expect(cb.parentElement?.childNodes[0]).toHaveClass('checkbox--disabled');
});

it('Has the class checkbox--checked', () => {
    const { getByText } = render(<CheckboxAtom {...props} checked />);

    const cb = getByText(/checkbox label/i);
    expect(cb.parentElement).toHaveClass('checkbox--checked');
});

it('Has the class checkbox--checked', () => {
    const { getByText } = render(<CheckboxAtom {...props} checked />);

    const cb = getByText(props.label);
    expect(cb).toHaveTextContent(props.label);
});

it('Has the class checkbox__container__hover when !disabled && !checked', () => {
    const { getByText } = render(<CheckboxAtom {...props} disabled={false} checked={false} />);

    const cb = getByText(/checkbox label/i);
    expect(cb.parentElement).toHaveClass('checkbox__container__hover');

});

it('Has the class checkbox--blue when checked & not disabled', () => {
    const { getByText } = render(<CheckboxAtom {...props} checked={true} />);

    const cb = getByText(/checkbox label/i);
    expect(cb.parentElement?.childNodes[0]).toHaveClass('checkbox--blue');
});

it('Has the class checkbox--white when disabled & not checked', () => {
    const { getByText } = render(<CheckboxAtom {...props} disabled checked={false} />);

    const cb = getByText(/checkbox label/i);
    expect(cb.parentElement?.childNodes[0]).toHaveClass('checkbox--white');
});

it('Has the class checkbox--gray when disabled and checked', () => {
    const { getByText } = render(<CheckboxAtom {...props} disabled checked />);

    const cb = getByText(/checkbox label/i);
    expect(cb.parentElement?.childNodes[0]).toHaveClass('checkbox--gray');
});

it('Clicks the CheckboxAtom', () => {
    const testObject = { counter: 0 };
    const { getByText } = render(<CheckboxAtom {...props} onClick={() => testObject.counter++} />);

    const cb = getByText(/checkbox label/i);
    fireEvent.click(cb);
    
    expect(testObject.counter).toBe(1);
});

it('Clicks the CheckboxAtom when disabled', () => {
    const testObject = { counter: 0 };
    const { getByText } = render(<CheckboxAtom {...props} onClick={() => testObject.counter++} disabled />);

    const cb = getByText(/checkbox label/i);
    fireEvent.click(cb);
    
    expect(testObject.counter).toBe(0);
});
