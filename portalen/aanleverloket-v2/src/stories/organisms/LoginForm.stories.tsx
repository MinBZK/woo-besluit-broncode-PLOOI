import { LoginFormOrganism } from '../../ui/organisms';
import { CenteredLayout } from '../../ui/layouts';
import { ComponentStory, ComponentMeta } from '@storybook/react';
import { Provider } from 'react-redux';
import {setupStore} from "../../store";
import { ContainerAtom } from '../../ui/atoms';

export default {
    title: 'KOOP-React/Organisms',
    component: LoginFormOrganism
} as ComponentMeta<typeof LoginFormOrganism>

export const LoginFormulier: ComponentStory<typeof LoginFormOrganism> = (args: any) => <CenteredLayout>
    <Provider store={setupStore()}>
        <ContainerAtom>
            <LoginFormOrganism {...args} />
        </ContainerAtom>
    </Provider>
</CenteredLayout>