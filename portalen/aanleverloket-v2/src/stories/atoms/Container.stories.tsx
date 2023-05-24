import { ContainerAtom } from '../../ui/atoms';
import { CenteredLayout } from '../../ui/layouts';
import { ComponentStory, ComponentMeta } from '@storybook/react';

export default {
    title: 'KOOP-React/Atoms',
    component: ContainerAtom,
    args: {
        children: '<ContainerAtom>Label in Container</ContainerAtom>',
        columns: false
    },
} as ComponentMeta<typeof ContainerAtom>

export const Container: ComponentStory<typeof ContainerAtom> = (args: any) => <CenteredLayout>
    <ContainerAtom {...args} />
</CenteredLayout>