import styled from 'styled-components';
import NavigationBar from '../../components/common/NavigationBar';
import Image from '../../components/common/Image';
import BoldText from '../../components/common/BoldText';
import Paragraph from '../../components/common/Paragraph';
import Button from '../../components/common/Button';
import FilteredArticle from '../../components/filter/FilteredArticle';

const Wrapper = styled.div`
  width: 320px;
  min-height: 568px;
  margin: 0 auto;
  padding: 0 16px;
  display: flex;
  flex-direction: column;
  gap: 11px;
  background-color: ${({ theme }) => theme.colors.primary};
  font-size: 18px;
  font-weight: 300;
  white-space: pre-wrap;
`;

const TitleBox = styled.div`
  width: 100%;
  margin-bottom: 32px;
`;

// Button 컴포넌트 상하 마진 주기 위한 div
const ButtonBox = styled.div`
  margin: 32px 0 16px 0;
`;

function ZipsaList({ onPrevious, onNext }) {
  return (
    <Wrapper>
      <NavigationBar
        leftContent={
          <Image
            width="40px"
            height="40px"
            margin="0 0 0 -12px"
            src={process.env.PUBLIC_URL + '/images/left_arrow.svg'}
          ></Image>
        }
        // rightContent는 없앴어요
        onPrevious={onPrevious}
        onNext={onNext}
      ></NavigationBar>

      <TitleBox>
        <Paragraph
          gap="5px"
          fontSize="35px"
          sentences={[
            <BoldText boldContent="최대 5명" normalContent="의"></BoldText>,
            '집사님을',
            '선택해 주세요',
          ]}
        ></Paragraph>
      </TitleBox>

      <FilteredArticle></FilteredArticle>
      <FilteredArticle></FilteredArticle>
      <FilteredArticle></FilteredArticle>

      <ButtonBox>
        <Button
          mode={'NORMAL_BLUE'}
          color={'#629af9'}
          msg={'2/5명에게 요청 보내기'}
        ></Button>
      </ButtonBox>
    </Wrapper>
  );
}

export default ZipsaList;
