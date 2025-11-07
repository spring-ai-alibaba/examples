import React from 'react';
import { Image, Modal, Button } from 'antd';
import { modalStyles } from './style';

interface TipsModalComponentProps {
  way: string,
  imageLink: string;
  isVisible: boolean;
  setModalVisible: (flag: boolean) => void;
}

const TipsModalComponent: React.FC<TipsModalComponentProps> = ({
  way = '',
  imageLink = '',
  isVisible,
  setModalVisible
}) => {

  return (
    <>
      <Modal
        title={way}
        open={isVisible}
        style={modalStyles.modal}
        closable={false}
        footer={() => {
          return <Button type="primary" onClick={() => setModalVisible(false)}>ok</Button>
        }}
      >
        <Image
          style={modalStyles.image}
          src={imageLink}
        />
      </Modal>
    </>
  );
};

export default TipsModalComponent;
