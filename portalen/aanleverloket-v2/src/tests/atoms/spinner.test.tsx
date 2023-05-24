import { render } from '@testing-library/react';
import { SpinnerAtom } from '../../ui/atoms';
import '@testing-library/jest-dom/extend-expect';


it('Renders the SpinnerAtom', () => {
   const {getByTestId} = render(<SpinnerAtom type={"primary"} />);

   const spinner = getByTestId("spinner");
   expect(spinner).toBeInTheDocument();
});