import { fireEvent, render, screen } from '@testing-library/react';
import { ButtonAtom } from '../../../ui/atoms/button';
import '@testing-library/jest-dom/extend-expect';

it('Renders the button', () => {
    const { getByRole } = render(<ButtonAtom onClick={() => { }} id={''}>
        Hello World
    </ButtonAtom>);

    const button = getByRole('button');
    expect(button).toBeInTheDocument();
});

it('Clicks the button', async () => {
    const testObject = {
        counter: 1,
        label: 'Hello World'
    }

    const { getByRole } = render(<ButtonAtom onClick={() => testObject.counter++} id={''}>{
        testObject.label
    }</ButtonAtom>);

    const button = getByRole('button');
    fireEvent.click(button);

    expect(testObject.counter).toBe(2);
});

it('Does not click the disabled button', async () => {
    const testObject = {
        counter: 1,
        label: 'Hello World'
    }

    const { getByRole } = render(<ButtonAtom disabled onClick={() => testObject.counter++} id={''}>{
        testObject.label
    }</ButtonAtom>);

    const button = getByRole('button');
    fireEvent.click(button);

    expect(testObject.counter).toBe(1);
});

it('Should be disabled', () => {
    const testObject = {
        label: 'Hello World'
    }

    const { getByRole } = render(<ButtonAtom type='primary' disabled onClick={() => { }} id={''}>{
        testObject.label
    }</ButtonAtom>);

    const button = getByRole('button');
    expect(button).toHaveProperty('disabled', true);
});

it('Should have primary class', () => {
    const { getByRole } = render(<ButtonAtom type='primary' disabled onClick={() => { }} id={''}>
        Hello World
    </ButtonAtom>);

    const button = getByRole('button');
    expect(button).toHaveClass('button--primary');
});

it('Should have unstyled class', () => {
    const { getByRole } = render(<ButtonAtom type='unstyled' onClick={() => { }} id={''}>
        Hello World
    </ButtonAtom>);

    const button = getByRole('button');
    expect(button).toHaveClass('button--unstyled');
});

it('Should have orange class', () => {
    const { getByRole } = render(<ButtonAtom type='orange' onClick={() => { } } id={''}>
        Hello World
    </ButtonAtom>);

    const button = getByRole('button');
    expect(button).toHaveClass('button--orange');
});

it('Renders the label', () => {
    const { getByRole } = render(<ButtonAtom
        children={"Hello World"}
        onClick={() => { } } id={''}    />);

    const bt = getByRole('button');
    expect(bt).toHaveTextContent('Hello World');
});